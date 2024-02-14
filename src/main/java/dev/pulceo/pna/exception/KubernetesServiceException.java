package dev.pulceo.pna.exception;

public class KubernetesServiceException extends Exception {
    public KubernetesServiceException() {
    }

    public KubernetesServiceException(String message) {
        super(message);
    }

    public KubernetesServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KubernetesServiceException(Throwable cause) {
        super(cause);
    }

    public KubernetesServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
