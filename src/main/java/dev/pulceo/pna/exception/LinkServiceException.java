package dev.pulceo.pna.exception;

public class LinkServiceException extends Exception {
    public LinkServiceException() {
        super();
    }

    public LinkServiceException(String message) {
        super(message);
    }

    public LinkServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkServiceException(Throwable cause) {
        super(cause);
    }

    protected LinkServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
