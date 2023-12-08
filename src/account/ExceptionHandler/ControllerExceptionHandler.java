package account.ExceptionHandler;


import account.Repository.EventRepository;
import account.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    EventRepository eventRepository;
    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Bad Request");
        body.put("exception", ex.getMessage());
        body.put("path", request.getDescription(false).substring(request.getDescription(false).indexOf('=')+1));
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(UserAlreadyFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleSignupUserExist(UserAlreadyFoundException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                request.getDescription(false)
        );
        log.info("[Exception | handleSignupUserExist] User already exist. method thrown");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleUserNotFound(UserNotFoundException e, WebRequest request){
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                request.getDescription(false).substring(request.getDescription(false).indexOf('=')+1)
        );
        log.info("[Exception | handleUserNotFound] User doesn't exist!");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleRoleNotFound(RoleNotFoundException e, WebRequest request){
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                e.getMessage(),
                request.getDescription(false).substring(request.getDescription(false).indexOf('=')+1)
        );
        log.info("[Exception | handleRoleNotFound] Role doesn't exist!");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalRemovalException.class)
    public ResponseEntity<CustomErrorMessage> handleIllegalRemoval(IllegalRemovalException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                request.getDescription(false).substring(request.getDescription(false).indexOf('=')+1)
        );
        log.info("[Exception | handleIllegalRemoval] Tried to delete a admin");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomErrorMessage> handleBadRequest(BadRequestException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                request.getDescription(false).substring(request.getDescription(false).indexOf('=')+1)
        );
        log.info("[Exception | handleBadRequest] " + e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordIsntDifferentException.class)
    public ResponseEntity<CustomErrorMessage> handlePasswordIsSame(PasswordIsntDifferentException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                request.getDescription(false).substring(request.getDescription(false).indexOf("=")+1)
        );
        log.info("[Exception | handlePasswordIsSame] New password is same as old password. method thrown");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BreachedPasswordException.class)
    public ResponseEntity<CustomErrorMessage> handlePasswordIsBreached(BreachedPasswordException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                request.getDescription(false)
        );
        log.info("[Exception | handlePasswordIsBreached] Password is breached. method thrown");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImportSalaryException.class)
    public ResponseEntity<CustomErrorMessage> handleImportSalaryException(ImportSalaryException e, WebRequest request) {
        CustomErrorMessage body = new CustomErrorMessage(
                LocalTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                request.getDescription(false).substring(request.getDescription(false).indexOf('=')+1)
        );
        log.info("[Exception | handleImportSalaryException] Salary list is invalid. method thrown");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
