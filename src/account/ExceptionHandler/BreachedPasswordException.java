package account.ExceptionHandler;

public class BreachedPasswordException extends RuntimeException {
    public BreachedPasswordException(String message) {
        super(message);
    }
}
