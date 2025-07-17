package br.com.consultdg.email_to_ftp_service.controller;

import br.com.consultdg.email_to_ftp_service.service.EmailToFtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/email-to-ftp")
public class EmailToFtpController {

    private static final Logger logger = LoggerFactory.getLogger(EmailToFtpController.class);

    @Autowired
    private EmailToFtpService emailToFtpService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "email-to-ftp-service",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processEmails() {
        logger.info("Processamento manual iniciado via API");
        
        try {
            emailToFtpService.processEmails();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Processamento iniciado com sucesso",
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            logger.error("Erro ao iniciar processamento manual: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Erro ao iniciar processamento: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<EmailToFtpService.ProcessingStats> getStats() {
        EmailToFtpService.ProcessingStats stats = emailToFtpService.getProcessingStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/stats/reset")
    public ResponseEntity<Map<String, Object>> resetStats() {
        emailToFtpService.resetStats();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Estatísticas zeradas com sucesso",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/test-connections")
    public ResponseEntity<Map<String, Object>> testConnections() {
        boolean success = emailToFtpService.testConnections();
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Todas as conexões testadas com sucesso",
                "timestamp", System.currentTimeMillis()
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "Falha em uma ou mais conexões",
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}
