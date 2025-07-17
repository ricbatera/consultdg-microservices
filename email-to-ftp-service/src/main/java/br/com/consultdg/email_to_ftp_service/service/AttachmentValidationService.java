package br.com.consultdg.email_to_ftp_service.service;

import br.com.consultdg.email_to_ftp_service.model.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AttachmentValidationService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentValidationService.class);
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "xml");
    private static final List<String> REQUIRED_PDF_PREFIXES = Arrays.asList("P_", "X_");

    /**
     * Valida um anexo de email baseado no nome do arquivo
     * 
     * @param fileName Nome do arquivo anexo
     * @return Resultado da validação
     */
    public ValidationResult validateAttachment(String fileName) {
        logger.debug("Validando anexo: {}", fileName);
        
        if (fileName == null || fileName.trim().isEmpty()) {
            return ValidationResult.builder()
                .valid(false)
                .reason("Nome do arquivo é nulo ou vazio")
                .invalidFileName(fileName)
                .build();
        }

        // Verificar se a extensão é permitida
        if (!isValidFileType(fileName)) {
            logger.info("✗ Anexo rejeitado por tipo não permitido: {}", fileName);
            return ValidationResult.builder()
                .valid(false)
                .reason("Tipo de arquivo não permitido. Apenas arquivos PDF e XML são aceitos.")
                .invalidFileName(fileName)
                .build();
        }

        // Se for PDF, validar nomenclatura
        if (isPdfFile(fileName)) {
            return validatePdfNaming(fileName);
        }

        // XML sempre válido por enquanto
        logger.debug("✓ Anexo XML válido: {}", fileName);
        return ValidationResult.builder()
            .valid(true)
            .build();
    }

    /**
     * Verifica se o tipo de arquivo é permitido (PDF ou XML)
     */
    private boolean isValidFileType(String fileName) {
        String lowerCase = fileName.toLowerCase();
        return ALLOWED_EXTENSIONS.stream().anyMatch(ext -> lowerCase.endsWith("." + ext));
    }

    /**
     * Verifica se é um arquivo PDF
     */
    private boolean isPdfFile(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }

    /**
     * Valida a nomenclatura de arquivos PDF
     * Padrão temporário: deve começar com P_ ou X_
     */
    private ValidationResult validatePdfNaming(String fileName) {
        boolean hasValidPrefix = REQUIRED_PDF_PREFIXES.stream()
            .anyMatch(prefix -> fileName.startsWith(prefix));

        if (hasValidPrefix) {
            logger.debug("✓ PDF com nomenclatura válida: {}", fileName);
            return ValidationResult.builder()
                .valid(true)
                .build();
        } else {
            logger.info("✗ PDF rejeitado por nomenclatura inválida: {}", fileName);
            return ValidationResult.builder()
                .valid(false)
                .reason("Nome do arquivo PDF deve começar com 'P_' ou 'X_'.")
                .invalidFileName(fileName)
                .build();
        }
    }

    /**
     * Retorna a lista de extensões permitidas
     */
    public List<String> getAllowedExtensions() {
        return ALLOWED_EXTENSIONS;
    }

    /**
     * Retorna a lista de prefixos obrigatórios para PDFs
     */
    public List<String> getRequiredPdfPrefixes() {
        return REQUIRED_PDF_PREFIXES;
    }
}
