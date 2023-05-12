package dev.pulceo.pna.exception;

public class SubProcessException extends Exception {
    public SubProcessException() {
        super();
    }

    public SubProcessException(String message) {
        super(message);
    }

    public SubProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubProcessException(Throwable cause) {
        super(cause);
    }

    protected SubProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
