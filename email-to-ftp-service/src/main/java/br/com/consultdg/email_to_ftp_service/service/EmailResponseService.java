package br.com.consultdg.email_to_ftp_service.service;

import br.com.consultdg.email_to_ftp_service.config.EmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.List;
import java.util.Properties;

@Service
public class EmailResponseService {

    private static final Logger logger = LoggerFactory.getLogger(EmailResponseService.class);

    @Autowired
    private EmailProperties emailProperties;

    /**
     * Envia email de resposta solicitando correção de nomenclatura
     * 
     * @param recipientEmail Email do destinatário
     * @param originalSubject Assunto original do email
     * @param invalidFileNames Lista de nomes de arquivos inválidos
     */
    public void sendNamingCorrectionRequest(String recipientEmail, String originalSubject, List<String> invalidFileNames) {
        try {
            logger.info("Enviando email de correção para: {} sobre {} arquivo(s) inválido(s)", recipientEmail, invalidFileNames.size());
            
            String responseSubject = buildResponseSubject(originalSubject);
            String responseBody = buildCorrectionRequestBody(invalidFileNames);
            
            sendEmail(recipientEmail, responseSubject, responseBody);
            
            logger.info("✓ Email de correção enviado com sucesso para: {}", recipientEmail);
            
        } catch (Exception e) {
            logger.error("✗ Erro ao enviar email de correção para {}: {}", recipientEmail, e.getMessage(), e);
        }
    }

    /**
     * Constrói o assunto da resposta
     */
    private String buildResponseSubject(String originalSubject) {
        return "RE: " + originalSubject + " - Correção de Nomenclatura Necessária";
    }

    /**
     * Constrói o corpo do email de correção
     */
    private String buildCorrectionRequestBody(List<String> invalidFileNames) {
        StringBuilder body = new StringBuilder();
        
        body.append("Prezado(a),\n\n");
        body.append("Recebemos seu email, porém ");
        
        if (invalidFileNames.size() == 1) {
            body.append("o seguinte arquivo PDF não está com a nomenclatura correta:\n\n");
        } else {
            body.append("os seguintes arquivos PDF não estão com a nomenclatura correta:\n\n");
        }
        
        invalidFileNames.forEach(fileName -> 
            body.append("• ").append(fileName).append("\n"));
        
        body.append("\n");
        body.append("PADRÃO EXIGIDO:\n");
        body.append("Os arquivos PDF devem começar com 'P_' ou 'X_' seguido do nome.\n\n");
        body.append("EXEMPLOS VÁLIDOS:\n");
        body.append("• P_123456_documento.pdf\n");
        body.append("• X_789012_relatorio.pdf\n\n");
        body.append("Por favor, renomeie ");
        
        if (invalidFileNames.size() == 1) {
            body.append("o arquivo seguindo");
        } else {
            body.append("os arquivos seguindo");
        }
        
        body.append(" o padrão acima e reenvie.\n\n");
        body.append("Arquivos XML são aceitos sem restrições de nomenclatura.\n\n");
        body.append("Aguardamos o reenvio com as correções.\n\n");
        body.append("Atenciosamente,\n");
        body.append("Sistema Automático de Processamento\n");
        body.append("ConsultDG Email to FTP Service");
        
        return body.toString();
    }

    /**
     * Envia o email usando as configurações do Gmail
     */
    private void sendEmail(String recipientEmail, String subject, String body) throws MessagingException {
        Properties props = createEmailProperties();
        
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    emailProperties.getImap().getUsername(),
                    emailProperties.getImap().getPassword()
                );
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailProperties.getImap().getUsername()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        
        logger.debug("Email enviado via SMTP para: {}", recipientEmail);
    }

    /**
     * Cria as propriedades para envio de email via SMTP
     */
    private Properties createEmailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
        return props;
    }

    /**
     * Testa se é possível enviar emails
     */
    public boolean testEmailSending() {
        try {
            Properties props = createEmailProperties();
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        emailProperties.getImap().getUsername(),
                        emailProperties.getImap().getPassword()
                    );
                }
            });

            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
            
            logger.info("✓ Teste de envio de email: SUCESSO");
            return true;
            
        } catch (Exception e) {
            logger.error("✗ Teste de envio de email: FALHA - {}", e.getMessage());
            return false;
        }
    }
}
