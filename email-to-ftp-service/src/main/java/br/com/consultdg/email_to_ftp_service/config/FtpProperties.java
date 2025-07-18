package br.com.consultdg.email_to_ftp_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ftp")
public class FtpProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private Remote remote = new Remote();

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

    public Remote getRemote() {
        return remote;
    }

    public void setRemote(Remote remote) {
        this.remote = remote;
    }

    public static class Remote {
        private String directory;

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }
    }
}
