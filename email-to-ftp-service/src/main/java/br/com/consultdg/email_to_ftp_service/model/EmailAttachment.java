package br.com.consultdg.email_to_ftp_service.model;

public class EmailAttachment {
    private String fileName;
    private String contentType;
    private long size;
    private byte[] data;

    public EmailAttachment() {
    }

    public EmailAttachment(String fileName, String contentType, long size, byte[] data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EmailAttachment{" +
                "fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                '}';
    }
}
