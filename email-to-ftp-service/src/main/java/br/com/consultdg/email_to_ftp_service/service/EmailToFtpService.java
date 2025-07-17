package br.com.consultdg.email_to_ftp_service.service;

import br.com.consultdg.email_to_ftp_service.config.EmailProperties;
import br.com.consultdg.email_to_ftp_service.model.EmailAttachment;
import br.com.consultdg.email_to_ftp_service.model.EmailMessage;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@EnableAsync
public class EmailToFtpService {

    private static final Logger logger = LoggerFactory.getLogger(EmailToFtpService.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private FtpService ftpService;

    @Autowired
    private EmailProperties emailProperties;

    private AtomicInteger processedEmails = new AtomicInteger(0);
    private AtomicInteger processedAttachments = new AtomicInteger(0);
    private AtomicInteger failedUploads = new AtomicInteger(0);

    @Scheduled(fixedDelayString = "${email.processing.interval}")
    public void processEmailsScheduled() {
        logger.info("Iniciando processamento agendado de emails");
        processEmails();
    }

    @Async
    public void processEmails() {
        try {
            logger.info("Buscando emails com anexos...");
            List<EmailMessage> emails = emailService.getEmailsWithAttachments();
            
            if (emails.isEmpty()) {
                logger.info("Nenhum email com anexos encontrado");
                return;
            }

            logger.info("Processando {} emails com anexos", emails.size());

            for (EmailMessage email : emails) {
                processEmail(email);
            }

            logger.info("Processamento concluído. Emails: {}, Anexos: {}, Falhas: {}", 
                       processedEmails.get(), processedAttachments.get(), failedUploads.get());

        } catch (MessagingException e) {
            logger.error("Erro ao buscar emails: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erro inesperado durante o processamento: {}", e.getMessage(), e);
        }
    }

    private void processEmail(EmailMessage email) {
        try {
            logger.info("Processando email: {} (de: {}, anexos: {})", 
                       email.getSubject(), email.getFrom(), email.getAttachments().size());

            boolean allUploadsSuccessful = true;

            for (EmailAttachment attachment : email.getAttachments()) {
                logger.info("Enviando anexo '{}' do email '{}' (tamanho: {})", 
                          attachment.getFileName(), email.getSubject(), attachment.getSize());
                
                boolean uploadSuccess = ftpService.uploadAttachment(attachment, email.getSubject(), email.getReceivedDate());
                
                if (uploadSuccess) {
                    processedAttachments.incrementAndGet();
                    logger.info("Anexo '{}' do email '{}' enviado com sucesso", 
                              attachment.getFileName(), email.getSubject());
                } else {
                    allUploadsSuccessful = false;
                    failedUploads.incrementAndGet();
                    logger.error("FALHA no envio do anexo '{}' do email '{}' - Verifique logs anteriores para detalhes do erro FTP", 
                               attachment.getFileName(), email.getSubject());
                }
            }

            if (allUploadsSuccessful) {
                email.setProcessed(true);
                processedEmails.incrementAndGet();
                logger.info("Email processado com sucesso: {}", email.getSubject());
            } else {
                logger.warn("Email processado com falhas: {}", email.getSubject());
            }

        } catch (Exception e) {
            logger.error("Erro ao processar email '{}': {}", email.getSubject(), e.getMessage(), e);
        }
    }

    public ProcessingStats getProcessingStats() {
        return new ProcessingStats(
            processedEmails.get(),
            processedAttachments.get(),
            failedUploads.get()
        );
    }

    public void resetStats() {
        processedEmails.set(0);
        processedAttachments.set(0);
        failedUploads.set(0);
        logger.info("Estatísticas de processamento zeradas");
    }

    public boolean testConnections() {
        logger.info("Testando conexões...");
        
        boolean ftpTest = ftpService.testConnection();
        logger.info("Teste FTP: {}", ftpTest ? "SUCESSO" : "FALHA");
        
        boolean emailTest = testEmailConnection();
        logger.info("Teste Email: {}", emailTest ? "SUCESSO" : "FALHA");
        
        return ftpTest && emailTest;
    }

    private boolean testEmailConnection() {
        try {
            emailService.getEmailsWithAttachments();
            return true;
        } catch (MessagingException e) {
            logger.error("Falha no teste de conexão de email: {}", e.getMessage());
            return false;
        }
    }

    public static class ProcessingStats {
        private final int processedEmails;
        private final int processedAttachments;
        private final int failedUploads;

        public ProcessingStats(int processedEmails, int processedAttachments, int failedUploads) {
            this.processedEmails = processedEmails;
            this.processedAttachments = processedAttachments;
            this.failedUploads = failedUploads;
        }

        public int getProcessedEmails() {
            return processedEmails;
        }

        public int getProcessedAttachments() {
            return processedAttachments;
        }

        public int getFailedUploads() {
            return failedUploads;
        }

        @Override
        public String toString() {
            return "ProcessingStats{" +
                    "processedEmails=" + processedEmails +
                    ", processedAttachments=" + processedAttachments +
                    ", failedUploads=" + failedUploads +
                    '}';
        }
    }
}
