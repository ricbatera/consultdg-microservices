package br.com.consultdg.email_to_ftp_service.service;

import br.com.consultdg.email_to_ftp_service.config.EmailProperties;
import br.com.consultdg.email_to_ftp_service.model.EmailAttachment;
import br.com.consultdg.email_to_ftp_service.model.EmailMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${email.processing.interval}")
    private long processingInterval;

    private AtomicInteger processedEmails = new AtomicInteger(0);
    private AtomicInteger processedAttachments = new AtomicInteger(0);
    private AtomicInteger failedUploads = new AtomicInteger(0);
    
    // Flag para evitar processamento concorrente
    private volatile boolean isProcessing = false;
    private volatile long processingStartTime = 0;
    private volatile long lastExecutionTime = 0;
    private volatile long lastExecutionDuration = 0;

    @Scheduled(fixedDelayString = "${email.processing.interval}")
    public void processEmailsScheduled() {
        logger.info("Iniciando processamento agendado de emails");
        processEmails();
    }

    @Async
    public void processEmails() {
        // Evita processamento concorrente
        if (isProcessing) {
            long processingTime = System.currentTimeMillis() - processingStartTime;
            logger.warn("Processamento já em andamento há {}ms, ignorando nova solicitação", processingTime);
            return;
        }
        
        isProcessing = true;
        long startTime = System.currentTimeMillis();
        processingStartTime = startTime;
        try {
            logger.info("Buscando emails com anexos...");
            List<EmailMessage> emails = emailService.getEmailsWithAttachments();
            
            long searchTime = System.currentTimeMillis() - startTime;
            logger.info("Busca de emails concluída em {}ms", searchTime);
            
            if (emails.isEmpty()) {
                logger.info("Nenhum email com anexos encontrado");
                return;
            }

            logger.info("Processando {} emails com anexos", emails.size());

            for (EmailMessage email : emails) {
                long emailStartTime = System.currentTimeMillis();
                try {
                    processEmail(email);
                    long emailProcessTime = System.currentTimeMillis() - emailStartTime;
                    logger.debug("Email '{}' processado em {}ms", email.getSubject(), emailProcessTime);
                } catch (Exception e) {
                    long emailProcessTime = System.currentTimeMillis() - emailStartTime;
                    logger.error("Erro ao processar email '{}' após {}ms: {}", email.getSubject(), emailProcessTime, e.getMessage(), e);
                }
            }

            logger.info("Processamento concluído. Emails: {}, Anexos: {}, Falhas: {}", 
                       processedEmails.get(), processedAttachments.get(), failedUploads.get());
            
            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("Tempo total de processamento: {}ms", totalTime);

        } catch (MessagingException e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("Erro ao buscar emails após {}ms: {}", totalTime, e.getMessage(), e);
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("Erro inesperado durante o processamento após {}ms: {}", totalTime, e.getMessage(), e);
        } finally {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("Processamento finalizado após {}ms. Flag isProcessing resetada.", totalTime);
            
            // Atualizar informações de execução
            lastExecutionTime = System.currentTimeMillis();
            lastExecutionDuration = totalTime;
            
            isProcessing = false;
            processingStartTime = 0;
        }
    }

    private void processEmail(EmailMessage email) {
        try {
            logger.info("=== INICIANDO PROCESSAMENTO DO EMAIL ===");
            logger.info("Assunto: '{}'", email.getSubject());
            logger.info("De: {}", email.getFrom());
            logger.info("Total de anexos: {}", email.getAttachments().size());

            boolean allUploadsSuccessful = true;
            int anexoIndex = 1;

            for (EmailAttachment attachment : email.getAttachments()) {
                logger.info(">>> Processando anexo {}/{}: '{}'", anexoIndex, email.getAttachments().size(), attachment.getFileName());
                logger.info("    Tamanho: {} bytes", attachment.getSize());
                
                boolean uploadSuccess = ftpService.uploadAttachment(attachment, email.getSubject(), email.getReceivedDate());
                
                if (uploadSuccess) {
                    processedAttachments.incrementAndGet();
                    logger.info("✓ Anexo '{}' do email '{}' enviado com SUCESSO (#{}/{})", 
                              attachment.getFileName(), email.getSubject(), anexoIndex, email.getAttachments().size());
                } else {
                    allUploadsSuccessful = false;
                    failedUploads.incrementAndGet();
                    logger.error("✗ FALHA no envio do anexo '{}' do email '{}' (#{}/{}) - Verifique logs anteriores para detalhes do erro FTP", 
                               attachment.getFileName(), email.getSubject(), anexoIndex, email.getAttachments().size());
                }
                anexoIndex++;
            }

            if (allUploadsSuccessful) {
                email.setProcessed(true);
                processedEmails.incrementAndGet();
                logger.info("✓ Email '{}' processado com SUCESSO - Todos os {} anexos foram enviados", 
                          email.getSubject(), email.getAttachments().size());
            } else {
                logger.warn("⚠ Email '{}' processado com FALHAS - Nem todos os anexos foram enviados", email.getSubject());
            }
            logger.info("=== FIM DO PROCESSAMENTO DO EMAIL ===");

        } catch (Exception e) {
            logger.error("ERRO CRÍTICO ao processar email '{}': {}", email.getSubject(), e.getMessage(), e);
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
            // Usa método de teste que não marca emails como lidos
            emailService.testConnection();
            return true;
        } catch (MessagingException e) {
            logger.error("Falha no teste de conexão de email: {}", e.getMessage());
            return false;
        }
    }

    public boolean isCurrentlyProcessing() {
        return isProcessing;
    }
    
    public long getProcessingTimeMs() {
        if (!isProcessing) return 0;
        return System.currentTimeMillis() - processingStartTime;
    }
    
    public void forceResetProcessingFlag() {
        if (isProcessing) {
            long processingTime = getProcessingTimeMs();
            logger.warn("Forçando reset da flag de processamento após {}ms - possível travamento detectado", processingTime);
            isProcessing = false;
            processingStartTime = 0;
        }
    }

    public SchedulingInfo getSchedulingInfo() {
        long nextExecutionTime = 0;
        if (lastExecutionTime > 0) {
            // Próxima execução = última execução + intervalo
            nextExecutionTime = lastExecutionTime + processingInterval;
        } else {
            // Se nunca executou, a próxima execução será em breve
            nextExecutionTime = System.currentTimeMillis() + processingInterval;
        }

        return new SchedulingInfo(
                lastExecutionTime,
                lastExecutionDuration,
                nextExecutionTime,
                processingInterval,
                isProcessing,
                processingStartTime > 0 ? System.currentTimeMillis() - processingStartTime : 0
        );
    }

    @Schema(description = "Estatísticas de processamento do serviço Email to FTP")
    public static class ProcessingStats {
        @Schema(description = "Número total de emails processados", example = "42")
        private final int processedEmails;
        
        @Schema(description = "Número total de anexos processados", example = "15")
        private final int processedAttachments;
        
        @Schema(description = "Número de uploads que falharam", example = "2")
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

    @Schema(description = "Informações de agendamento e execução do serviço")
    public static class SchedulingInfo {
        @Schema(description = "Timestamp da última execução (0 se nunca executou)", example = "1642781234567")
        private final long lastExecutionTime;
        
        @Schema(description = "Duração da última execução em ms", example = "15000")
        private final long lastExecutionDuration;
        
        @Schema(description = "Timestamp da próxima execução estimada", example = "1642781294567")
        private final long nextExecutionTime;
        
        @Schema(description = "Intervalo de processamento configurado em ms", example = "60000")
        private final long processingInterval;
        
        @Schema(description = "Se está processando atualmente", example = "false")
        private final boolean isCurrentlyProcessing;
        
        @Schema(description = "Tempo atual de processamento em ms (se estiver processando)", example = "5000")
        private final long currentProcessingTime;

        public SchedulingInfo(long lastExecutionTime, long lastExecutionDuration, 
                             long nextExecutionTime, long processingInterval,
                             boolean isCurrentlyProcessing, long currentProcessingTime) {
            this.lastExecutionTime = lastExecutionTime;
            this.lastExecutionDuration = lastExecutionDuration;
            this.nextExecutionTime = nextExecutionTime;
            this.processingInterval = processingInterval;
            this.isCurrentlyProcessing = isCurrentlyProcessing;
            this.currentProcessingTime = currentProcessingTime;
        }

        public long getLastExecutionTime() { return lastExecutionTime; }
        public long getLastExecutionDuration() { return lastExecutionDuration; }
        public long getNextExecutionTime() { return nextExecutionTime; }
        public long getProcessingInterval() { return processingInterval; }
        public boolean isCurrentlyProcessing() { return isCurrentlyProcessing; }
        public long getCurrentProcessingTime() { return currentProcessingTime; }

        @Override
        public String toString() {
            return "SchedulingInfo{" +
                    "lastExecutionTime=" + lastExecutionTime +
                    ", lastExecutionDuration=" + lastExecutionDuration +
                    ", nextExecutionTime=" + nextExecutionTime +
                    ", processingInterval=" + processingInterval +
                    ", isCurrentlyProcessing=" + isCurrentlyProcessing +
                    ", currentProcessingTime=" + currentProcessingTime +
                    '}';
        }
    }
}
