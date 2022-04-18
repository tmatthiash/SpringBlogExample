package com.springboot.blog.exception;

import java.io.ObjectInputStream.GetField;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.springboot.blog.payload.ErrorDetails;

import org.apache.coyote.ErrorState;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity <ErrorDetails> handleResourceNotFoundException(
        ResourceNotFoundException exception,
        WebRequest request) {
            ErrorDetails errorDetails = 
            new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));

            return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BlogAPIException.class)
    public ResponseEntity <ErrorDetails> handleBlogAPIException(
        BlogAPIException exception,
        WebRequest request) {
            ErrorDetails errorDetails = 
            new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));

            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request) {
            
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach((err) -> {
                String fieldName = ((FieldError)err).getField();
                String message = err.getDefaultMessage();
                errors.put(fieldName, message);
            });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity <ErrorDetails> handleGlobalException(
        Exception exception,
        WebRequest request) {
            ErrorDetails errorDetails = 
            new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));

            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
