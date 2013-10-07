package at.ffesternberg.sybos.exception;

public class NoSybosTokenException extends SybosClientException {
    public NoSybosTokenException() {
        super("No sybos Token given! Please set a valid sybos Token!");
    }
}
