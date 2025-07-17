package br.com.consultdg.email_to_ftp_service.service;

import br.com.consultdg.email_to_ftp_service.config.EmailProperties;
import br.com.consultdg.email_to_ftp_service.model.EmailAttachment;
import br.com.consultdg.email_to_ftp_service.model.EmailMessage;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SubjectTerm;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmailProperties emailProperties;

    private Session session;
    private Store store;

    private Session getSession() {
        if (session == null) {
            Properties props = new Properties();
            props.put("mail.store.protocol", emailProperties.getImap().getProtocol());
            props.put("mail.imap.host", emailProperties.getImap().getHost());
            props.put("mail.imap.port", emailProperties.getImap().getPort());
            props.put("mail.imap.ssl.enable", "true");
            props.put("mail.imap.starttls.enable", "true");
            props.put("mail.imap.auth", "true");
            // Removendo OAUTH2 para usar autenticação básica com senha de app
            // props.put("mail.imap.auth.mechanisms", "XOAUTH2");
            // props.put("mail.imap.sasl.enable", "true");
            // props.put("mail.imap.sasl.mechanisms", "XOAUTH2");
            props.put("mail.imap.auth.plain.disable", "false");
            // props.put("mail.imap.auth.ntlm.disable", "true");

            session = Session.getInstance(props);
        }
        return session;
    }

    private Store getStore() throws MessagingException {
        if (store == null || !store.isConnected()) {
            store = getSession().getStore(emailProperties.getImap().getProtocol());
            store.connect(
                emailProperties.getImap().getHost(),
                emailProperties.getImap().getPort(),
                emailProperties.getImap().getUsername(),
                emailProperties.getImap().getPassword()
            );
        }
        return store;
    }

    public List<EmailMessage> getEmailsWithAttachments() throws MessagingException {
        List<EmailMessage> emails = new ArrayList<>();
        
        try {
            Store store = getStore();
            Folder folder = store.getFolder(emailProperties.getImap().getFolder());
            folder.open(Folder.READ_WRITE);

            // Buscar apenas emails não lidos com anexos
            SearchTerm searchTerm = new AndTerm(
                new FlagTerm(new Flags(Flags.Flag.SEEN), false),
                new SubjectTerm("*") // Todos os assuntos
            );

            Message[] messages = folder.search(searchTerm);
            
            logger.info("Encontrados {} emails não lidos", messages.length);

            int processedCount = 0;
            int maxEmails = Math.min(messages.length, emailProperties.getProcessing().getMaxMessages());
            logger.info("Processando no máximo {} emails", maxEmails);
            
            for (Message message : messages) {
                if (processedCount >= maxEmails) {
                    logger.info("Limite de emails atingido: {}", maxEmails);
                    break;
                }

                try {
                    EmailMessage emailMessage = processMessage(message);
                    if (emailMessage != null && !emailMessage.getAttachments().isEmpty()) {
                        emails.add(emailMessage);
                        processedCount++;
                        logger.info("Email adicionado para processamento: {} com {} anexos", 
                                   emailMessage.getSubject(), emailMessage.getAttachments().size());
                    } else if (emailMessage != null) {
                        logger.debug("Email sem anexos ignorado: {}", emailMessage.getSubject());
                    }
                } catch (Exception e) {
                    logger.error("Erro ao processar email: {}", e.getMessage(), e);
                }
                
                // Força saída se já processamos emails suficientes
                if (emails.size() >= 5) {
                    logger.info("Coletados {} emails com anexos. Interrompendo coleta para iniciar processamento.", emails.size());
                    break;
                }
            }

            folder.close(false);
            logger.info("Processados {} emails com anexos", emails.size());

        } catch (MessagingException e) {
            logger.error("Erro ao conectar ao servidor de email: {}", e.getMessage(), e);
            throw e;
        }

        return emails;
    }

    private EmailMessage processMessage(Message message) throws MessagingException, IOException {
        String subject = message.getSubject();
        String from = message.getFrom()[0].toString();
        String to = message.getAllRecipients()[0].toString();
        LocalDateTime receivedDate = message.getReceivedDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        List<EmailAttachment> attachments = new ArrayList<>();
        String body = "";

        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            body = processMultipart(multipart, attachments);
        } else {
            body = message.getContent().toString();
        }

        EmailMessage emailMessage = new EmailMessage(subject, from, to, receivedDate, body, attachments);
        
        // Marcar como lido se configurado
        if (emailProperties.getProcessing().isMarkAsRead() && !attachments.isEmpty()) {
            message.setFlag(Flags.Flag.SEEN, true);
        }

        return emailMessage;
    }

    private String processMultipart(Multipart multipart, List<EmailAttachment> attachments) 
            throws MessagingException, IOException {
        StringBuilder body = new StringBuilder();
        
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) || 
                (bodyPart.getFileName() != null && !bodyPart.getFileName().isEmpty())) {
                
                // É um anexo
                String fileName = bodyPart.getFileName();
                if (fileName != null) {
                    String contentType = bodyPart.getContentType();
                    long size = bodyPart.getSize();
                    
                    try (InputStream inputStream = bodyPart.getInputStream()) {
                        byte[] data = IOUtils.toByteArray(inputStream);
                        EmailAttachment attachment = new EmailAttachment(fileName, contentType, size, data);
                        attachments.add(attachment);
                        logger.debug("Anexo encontrado: {} ({})", fileName, contentType);
                    }
                }
            } else if (bodyPart.isMimeType("text/plain")) {
                // É texto simples
                body.append(bodyPart.getContent().toString());
            } else if (bodyPart.isMimeType("text/html")) {
                // É HTML
                body.append(bodyPart.getContent().toString());
            } else if (bodyPart.isMimeType("multipart/*")) {
                // É uma multipart aninhada
                MimeMultipart nestedMultipart = (MimeMultipart) bodyPart.getContent();
                body.append(processMultipart(nestedMultipart, attachments));
            }
        }
        
        return body.toString();
    }

    public void closeConnection() {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (MessagingException e) {
            logger.error("Erro ao fechar conexão com o servidor de email: {}", e.getMessage());
        }
    }
}
