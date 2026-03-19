package com.example.gamelibrary.exception;

import com.example.gamelibrary.exception.response.ErrorResponse;
import com.example.gamelibrary.exception.response.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String VALIDATION_FAILED = "Validation failed";
    private static final String MALFORMED_JSON_REQUEST = "Malformed JSON request";
    private static final String ENDPOINT_NOT_FOUND = "Endpoint not found";
    private static final String DATA_INTEGRITY_VIOLATION = "Operation violates data integrity constraints";
    private static final String UNEXPECTED_SERVER_ERROR = "An unexpected error occurred";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            mergeError(errors, fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Validation failed for {}: {}", request.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(validationErrorResponse(errors));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleHandlerMethodValidation(
            HandlerMethodValidationException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (ParameterValidationResult validationResult : ex.getParameterValidationResults()) {
            String parameterName = validationResult.getMethodParameter().getParameterName();
            String key = parameterName != null ? parameterName : "parameter";

            if (validationResult instanceof ParameterErrors parameterErrors && parameterErrors.hasFieldErrors()) {
                for (FieldError fieldError : parameterErrors.getFieldErrors()) {
                    mergeError(errors, key + "." + fieldError.getField(), fieldError.getDefaultMessage());
                }
                continue;
            }

            for (MessageSourceResolvable error : validationResult.getResolvableErrors()) {
                mergeError(errors, key, error.getDefaultMessage());
            }
        }
        log.warn("Method validation failed for {}: {}", request.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(validationErrorResponse(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "param";
            mergeError(errors, path, violation.getMessage());
        });
        log.warn("Constraint violation for {}: {}", request.getRequestURI(), errors);
        return ResponseEntity.badRequest().body(validationErrorResponse(errors));
    }

    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        log.warn("Bad request for {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("Malformed JSON for {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse(HttpStatus.BAD_REQUEST, MALFORMED_JSON_REQUEST));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        log.warn("Method not allowed for {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Endpoint not found for {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse(HttpStatus.NOT_FOUND, ENDPOINT_NOT_FOUND));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation for {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponse(HttpStatus.CONFLICT, DATA_INTEGRITY_VIOLATION));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        log.warn("Response status exception for {}: {}", request.getRequestURI(), message);
        return ResponseEntity.status(status).body(errorResponse(status, message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(
                ex.getClass(),
                ResponseStatus.class
        );
        HttpStatus status = responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = status.is5xxServerError() ? UNEXPECTED_SERVER_ERROR : ex.getMessage();

        if (status.is5xxServerError()) {
            log.error("Unhandled runtime exception for {}", request.getRequestURI(), ex);
        } else {
            log.warn("Runtime exception for {}: {}", request.getRequestURI(), ex.getMessage());
        }

        return ResponseEntity.status(status).body(errorResponse(status, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for {}", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_SERVER_ERROR));
    }

    private ErrorResponse errorResponse(HttpStatus status, String message) {
        return new ErrorResponse(
                status.value(),
                message != null ? message : status.getReasonPhrase(),
                LocalDateTime.now()
        );
    }

    private ValidationErrorResponse validationErrorResponse(Map<String, String> errors) {
        return new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                VALIDATION_FAILED,
                LocalDateTime.now(),
                Map.copyOf(errors)
        );
    }

    private void mergeError(Map<String, String> errors, String field, String message) {
        String safeField = field != null ? field : "field";
        String safeMessage = message != null ? message : "Invalid value";
        errors.merge(safeField, safeMessage, (oldValue, newValue) -> oldValue + "; " + newValue);
    }
}
