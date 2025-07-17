package br.com.consultdg.email_to_ftp_service.service;

import br.com.consultdg.email_to_ftp_service.config.FtpProperties;
import br.com.consultdg.email_to_ftp_service.model.EmailAttachment;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FtpService {

    private static final Logger logger = LoggerFactory.getLogger(FtpService.class);

    @Autowired
    private FtpProperties ftpProperties;

    public boolean uploadAttachment(EmailAttachment attachment, String emailSubject, LocalDateTime receivedDate) {
        FTPClient ftpClient = new FTPClient();
        
        try {
            // Conectar ao servidor FTP
            ftpClient.connect(ftpProperties.getHost(), ftpProperties.getPort());
            
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.error("Falha ao conectar ao servidor FTP para anexo '{}' do email '{}'. Motivo: Código de resposta inválido ({})", 
                           attachment.getFileName(), emailSubject, reply);
                return false;
            }

            // Fazer login
            boolean loginSuccess = ftpClient.login(ftpProperties.getUsername(), ftpProperties.getPassword());
            if (!loginSuccess) {
                logger.error("Falha no login do FTP para anexo '{}' do email '{}'. Motivo: Credenciais inválidas ou acesso negado", 
                           attachment.getFileName(), emailSubject);
                return false;
            }

            // Configurar modo passivo e binário
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Criar diretório se não existir
            String remoteDirectory = ftpProperties.getRemote().getDirectory();
            if (!ftpClient.changeWorkingDirectory(remoteDirectory)) {
                boolean created = ftpClient.makeDirectory(remoteDirectory);
                if (!created) {
                    logger.error("Falha ao criar diretório remoto '{}' para anexo '{}' do email '{}'. Motivo: Permissão negada ou diretório pai inexistente", 
                               remoteDirectory, attachment.getFileName(), emailSubject);
                    return false;
                }
                ftpClient.changeWorkingDirectory(remoteDirectory);
            }

            // Criar subdiretório baseado na data
            String dateDir = receivedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String fullPath = remoteDirectory + "/" + dateDir;
            
            if (!ftpClient.changeWorkingDirectory(fullPath)) {
                boolean created = ftpClient.makeDirectory(dateDir);
                if (!created) {
                    logger.warn("Falha ao criar diretório de data '{}' para anexo '{}' do email '{}'. Motivo: Permissão negada ou erro no servidor", 
                              dateDir, attachment.getFileName(), emailSubject);
                } else {
                    ftpClient.changeWorkingDirectory(fullPath);
                }
            }

            // Gerar nome único para o arquivo
            String timestamp = receivedDate.format(DateTimeFormatter.ofPattern("HHmmss"));
            String cleanSubject = cleanFileName(emailSubject);
            String fileName = String.format("%s_%s_%s", timestamp, cleanSubject, attachment.getFileName());
            fileName = fileName.length() > 255 ? fileName.substring(0, 255) : fileName;

            // Fazer upload do arquivo
            ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment.getData());
            boolean uploaded = ftpClient.storeFile(fileName, inputStream);
            
            if (uploaded) {
                logger.info("Anexo '{}' do email '{}' enviado com sucesso para FTP (tamanho: {})", 
                          attachment.getFileName(), emailSubject, attachment.getSize());
                return true;
            } else {
                logger.error("Falha ao enviar anexo '{}' do email '{}' para FTP. Motivo: Erro na transferência do arquivo ou espaço insuficiente no servidor", 
                           attachment.getFileName(), emailSubject);
                return false;
            }

        } catch (IOException e) {
            logger.error("Erro de IO durante upload FTP do anexo '{}' do email '{}'. Motivo: {} - Detalhes: {}", 
                       attachment.getFileName(), emailSubject, e.getClass().getSimpleName(), e.getMessage(), e);
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                logger.error("Erro ao fechar conexão FTP: {}", e.getMessage());
            }
        }
    }

    private String cleanFileName(String fileName) {
        if (fileName == null) return "unknown";
        
        // Remove caracteres inválidos para nomes de arquivo
        String cleaned = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Limita o tamanho
        if (cleaned.length() > 50) {
            cleaned = cleaned.substring(0, 50);
        }
        
        return cleaned;
    }

    public boolean testConnection() {
        FTPClient ftpClient = new FTPClient();
        
        try {
            ftpClient.connect(ftpProperties.getHost(), ftpProperties.getPort());
            
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.error("Falha ao conectar ao servidor FTP. Código de resposta: {}", reply);
                return false;
            }

            boolean loginSuccess = ftpClient.login(ftpProperties.getUsername(), ftpProperties.getPassword());
            if (!loginSuccess) {
                logger.error("Falha no login do FTP");
                return false;
            }

            logger.info("Conexão FTP testada com sucesso");
            return true;

        } catch (IOException e) {
            logger.error("Erro ao testar conexão FTP: {}", e.getMessage(), e);
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                logger.error("Erro ao fechar conexão FTP de teste: {}", e.getMessage());
            }
        }
    }
}
