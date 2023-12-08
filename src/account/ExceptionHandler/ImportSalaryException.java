package account.ExceptionHandler;

public class ImportSalaryException extends RuntimeException {
    public ImportSalaryException(String errorMessage) {
        super(errorMessage);
    }
}
