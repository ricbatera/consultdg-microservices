package br.com.consultdg.email_to_ftp_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    private Imap imap = new Imap();
    private Processing processing = new Processing();

    public Imap getImap() {
        return imap;
    }

    public void setImap(Imap imap) {
        this.imap = imap;
    }

    public Processing getProcessing() {
        return processing;
    }

    public void setProcessing(Processing processing) {
        this.processing = processing;
    }

    public static class Imap {
        private String host;
        private int port;
        private String username;
        private String password;
        private String folder;
        private String protocol;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }
    }

    public static class Processing {
        private long interval;
        private int maxMessages;
        private boolean markAsRead;

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public int getMaxMessages() {
            return maxMessages;
        }

        public void setMaxMessages(int maxMessages) {
            this.maxMessages = maxMessages;
        }

        public boolean isMarkAsRead() {
            return markAsRead;
        }

        public void setMarkAsRead(boolean markAsRead) {
            this.markAsRead = markAsRead;
        }
    }
}
