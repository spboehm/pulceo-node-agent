package dev.pulceo.pna.exception;

public class PingException extends Exception {
    public PingException() {
        super();
    }

    public PingException(String message) {
        super(message);
    }

    public PingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PingException(Throwable cause) {
        super(cause);
    }

    protected PingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
