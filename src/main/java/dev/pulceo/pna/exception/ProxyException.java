package dev.pulceo.pna.exception;

public class ProxyException extends Exception {
    public ProxyException() {
    }

    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }

    public ProxyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
