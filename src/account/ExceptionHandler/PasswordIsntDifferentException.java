package account.ExceptionHandler;

public class PasswordIsntDifferentException extends RuntimeException {
    public PasswordIsntDifferentException(String message) {
        super(message);
    }
}
