package dev.pulceo.pna.exception;

public class CloudRegistrationException extends Exception {
    public CloudRegistrationException() {
        super();
    }

    public CloudRegistrationException(String message) {
        super(message);
    }

    public CloudRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudRegistrationException(Throwable cause) {
        super(cause);
    }

    protected CloudRegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
