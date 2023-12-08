package account.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserAlreadyFoundException extends RuntimeException{
    public UserAlreadyFoundException(String message){
        super(message);
    }
}
