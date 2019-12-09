package io.zipcoder.tc_spring_poll_application.exception;


import dtos.error.ErrorDetail;
import dtos.error.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException rnfe, HttpServletRequest request) {
        ErrorDetail err = new ErrorDetail();
        err.setDetail(rnfe.getMessage());
        err.setTitle("ResourceNotFoundException");
        err.setStatus(404);
        err.setTimeStamp(new Date().getTime());
        err.setDeveloperMessage(rnfe.getStackTrace().toString());

        return new ResponseEntity<>(err, null, HttpStatus.NOT_FOUND);


    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException manve, HttpServletRequest request) {
        ErrorDetail err = new ErrorDetail();
        List<FieldError> fieldErrors = manve.getBindingResult().getFieldErrors();

        for (FieldError error : fieldErrors) {
            List<ValidationError> valErrors = err.getErrors().get(error.getField());

            if (valErrors == null) {
                valErrors = new ArrayList<>();
                err.getErrors().put(error.getField(), valErrors);
            }
            ValidationError validationError = new ValidationError();
            validationError.setCode(error.getCode());
            validationError.setMessage(messageSource.getMessage(error, null));
            valErrors.add(validationError);

        }
        return new ResponseEntity<>(err, null, HttpStatus.NOT_FOUND);

    }

}
