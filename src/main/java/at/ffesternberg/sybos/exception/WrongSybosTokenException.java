package at.ffesternberg.sybos.exception;

public class WrongSybosTokenException extends SybosClientException {
    public WrongSybosTokenException() {
        super("Wrong syBos Token given!");
    }

    public WrongSybosTokenException(String token) {
        super("Wrong syBos Token given! (Token was: " + token + ")");
    }
}
