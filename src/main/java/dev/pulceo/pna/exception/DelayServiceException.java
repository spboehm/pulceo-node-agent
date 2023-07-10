package dev.pulceo.pna.exception;

public class DelayServiceException extends Exception {
    public DelayServiceException() {
        super();
    }

    public DelayServiceException(String message) {
        super(message);
    }

    public DelayServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DelayServiceException(Throwable cause) {
        super(cause);
    }

    protected DelayServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
