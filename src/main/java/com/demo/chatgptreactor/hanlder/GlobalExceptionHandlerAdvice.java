package com.demo.chatgptreactor.hanlder;

import com.demo.chatgptreactor.result.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleBindException(WebExchangeBindException e) {
        StringBuilder errors = new StringBuilder("Validation failed for object='")
                .append(e.getObjectName())
                .append("'. Error count: ")
                .append(e.getErrorCount())
                .append(". Errors: ");

        for (FieldError error : e.getFieldErrors()) {
            errors.append("[Field = ")
                    .append(error.getField())
                    .append(", Message = ")
                    .append(error.getDefaultMessage())
                    .append("]; ");
        }

        return ResponseEntity.badRequest().body(errors.toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorText(e.getMessage());
        errorResponse.setStackTrace(e.getStackTrace());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorText(e.getErrorCode());
        errorResponse.setStackTrace(e.getStackTrace());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }
}