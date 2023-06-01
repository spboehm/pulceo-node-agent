package dev.pulceo.pna.exception;

public class NpingException extends Exception {
    public NpingException() {
        super();
    }

    public NpingException(String message) {
        super(message);
    }

    public NpingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NpingException(Throwable cause) {
        super(cause);
    }

    protected NpingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
