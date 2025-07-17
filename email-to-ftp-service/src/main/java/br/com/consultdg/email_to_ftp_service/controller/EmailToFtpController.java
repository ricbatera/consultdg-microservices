package br.com.consultdg.email_to_ftp_service.controller;

import br.com.consultdg.email_to_ftp_service.service.EmailToFtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/email-to-ftp")
@Tag(name = "Email to FTP Service", description = "APIs para gerenciamento do serviço de processamento de emails e transferência de anexos via FTP")
public class EmailToFtpController {

    private static final Logger logger = LoggerFactory.getLogger(EmailToFtpController.class);

    @Autowired
    private EmailToFtpService emailToFtpService;

    @Operation(summary = "Verificar saúde do serviço", description = "Endpoint para verificar se o serviço está funcionando corretamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço funcionando normalmente", 
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "email-to-ftp-service",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @Operation(summary = "Processar emails manualmente", description = "Inicia o processamento manual de emails")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Processamento iniciado com sucesso",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json"))
    })
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

    @Operation(summary = "Obter estatísticas de processamento", description = "Retorna as estatísticas atuais do processamento de emails")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas obtidas com sucesso",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = EmailToFtpService.ProcessingStats.class)))
    })
    @GetMapping("/stats")
    public ResponseEntity<EmailToFtpService.ProcessingStats> getStats() {
        EmailToFtpService.ProcessingStats stats = emailToFtpService.getProcessingStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Zerar estatísticas", description = "Reseta todas as estatísticas de processamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estatísticas zeradas com sucesso",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/stats/reset")
    public ResponseEntity<Map<String, Object>> resetStats() {
        emailToFtpService.resetStats();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Estatísticas zeradas com sucesso",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @Operation(summary = "Testar conexões", description = "Testa as conexões com o servidor de email e FTP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todas as conexões testadas com sucesso",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Falha em uma ou mais conexões",
                    content = @Content(mediaType = "application/json"))
    })
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

    @Operation(summary = "Obter status do processamento", description = "Retorna o status atual do processamento incluindo estatísticas e tempo de execução")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status obtido com sucesso",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getProcessingStatus() {
        boolean isProcessing = emailToFtpService.isCurrentlyProcessing();
        long processingTimeMs = emailToFtpService.getProcessingTimeMs();
        EmailToFtpService.ProcessingStats stats = emailToFtpService.getProcessingStats();
        
        return ResponseEntity.ok(Map.of(
            "isProcessing", isProcessing,
            "processingTimeMs", processingTimeMs,
            "processingTimeSeconds", processingTimeMs / 1000,
            "stats", stats,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @Operation(summary = "Forçar reset do processamento", description = "Força o reset da flag de processamento em caso de travamento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Flag de processamento resetada com sucesso",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/force-reset")
    public ResponseEntity<Map<String, Object>> forceReset() {
        logger.warn("Forçando reset do processamento via API");
        emailToFtpService.forceResetProcessingFlag();
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Flag de processamento resetada com sucesso",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @Operation(summary = "Obter informações de agendamento", description = "Retorna informações sobre a última execução e próxima execução agendada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações de agendamento obtidas com sucesso",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = EmailToFtpService.SchedulingInfo.class)))
    })
    @GetMapping("/scheduling")
    public ResponseEntity<EmailToFtpService.SchedulingInfo> getSchedulingInfo() {
        EmailToFtpService.SchedulingInfo schedulingInfo = emailToFtpService.getSchedulingInfo();
        return ResponseEntity.ok(schedulingInfo);
    }
}
