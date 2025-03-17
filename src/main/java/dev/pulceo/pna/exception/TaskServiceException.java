package dev.pulceo.pna.exception;

public class TaskServiceException extends Exception {

    public TaskServiceException() {
    }

    public TaskServiceException(String message) {
        super(message);
    }

    public TaskServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskServiceException(Throwable cause) {
        super(cause);
    }

    public TaskServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
