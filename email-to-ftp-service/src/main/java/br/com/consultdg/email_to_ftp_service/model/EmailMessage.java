package br.com.consultdg.email_to_ftp_service.model;

import java.time.LocalDateTime;
import java.util.List;

public class EmailMessage {
    private String subject;
    private String from;
    private String to;
    private LocalDateTime receivedDate;
    private String body;
    private List<EmailAttachment> attachments;
    private boolean processed;

    public EmailMessage() {
    }

    public EmailMessage(String subject, String from, String to, LocalDateTime receivedDate, String body, List<EmailAttachment> attachments) {
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.receivedDate = receivedDate;
        this.body = body;
        this.attachments = attachments;
        this.processed = false;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmailAttachment> attachments) {
        this.attachments = attachments;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public String toString() {
        return "EmailMessage{" +
                "subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", receivedDate=" + receivedDate +
                ", attachments=" + (attachments != null ? attachments.size() : 0) + " attachments" +
                ", processed=" + processed +
                '}';
    }
}
