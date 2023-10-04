package dev.pulceo.pna.exception;

public class PingServiceException extends Exception {
    public PingServiceException() {
        super();
    }

    public PingServiceException(String message) {
        super(message);
    }

    public PingServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PingServiceException(Throwable cause) {
        super(cause);
    }

    protected PingServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
