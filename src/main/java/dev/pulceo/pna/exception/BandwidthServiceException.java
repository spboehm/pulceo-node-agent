package dev.pulceo.pna.exception;


public class BandwidthServiceException extends Exception {
    public BandwidthServiceException() {
        super();
    }

    public BandwidthServiceException(String message) {
        super(message);
    }

    public BandwidthServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BandwidthServiceException(Throwable cause) {
        super(cause);
    }

    protected BandwidthServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
