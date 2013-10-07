package at.ffesternberg.sybos.exception;

public class SybosClientException extends Exception {
    public SybosClientException() {
        super("An error occured in the sybos Client");
    }

    public SybosClientException(String message) {
        super(message);
    }

    public SybosClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
