package dev.pulceo.pna.exception;

public class ResourceServiceUtilizationException extends Exception {
    public ResourceServiceUtilizationException() {
    }

    public ResourceServiceUtilizationException(String message) {
        super(message);
    }

    public ResourceServiceUtilizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceServiceUtilizationException(Throwable cause) {
        super(cause);
    }

    public ResourceServiceUtilizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
