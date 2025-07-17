package br.com.consultdg.email_to_ftp_service.model;

/**
 * Resultado da validação de um anexo de email
 */
public class ValidationResult {
    private boolean valid;
    private String reason;
    private String invalidFileName;

    public ValidationResult() {}

    public ValidationResult(boolean valid, String reason, String invalidFileName) {
        this.valid = valid;
        this.reason = reason;
        this.invalidFileName = invalidFileName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getInvalidFileName() {
        return invalidFileName;
    }

    public void setInvalidFileName(String invalidFileName) {
        this.invalidFileName = invalidFileName;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", reason='" + reason + '\'' +
                ", invalidFileName='" + invalidFileName + '\'' +
                '}';
    }

    public static class Builder {
        private boolean valid;
        private String reason;
        private String invalidFileName;

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder invalidFileName(String invalidFileName) {
            this.invalidFileName = invalidFileName;
            return this;
        }

        public ValidationResult build() {
            return new ValidationResult(valid, reason, invalidFileName);
        }
    }
}
