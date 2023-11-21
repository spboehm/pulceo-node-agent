package dev.pulceo.pna.exception;

public class PNAException extends Exception {

    public PNAException() {
        super();
    }

    public PNAException(String message) {
        super(message);
    }

    public PNAException(String message, Throwable cause) {
        super(message, cause);
    }

    public PNAException(Throwable cause) {
        super(cause);
    }

    protected PNAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
