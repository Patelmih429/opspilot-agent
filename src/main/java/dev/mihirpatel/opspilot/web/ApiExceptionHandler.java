package dev.mihirpatel.opspilot.web;

import dev.mihirpatel.opspilot.incident.IncidentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IncidentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError notFound(IncidentNotFoundException exception) {
        return new ApiError("incident_not_found", exception.getMessage(), Map.of(), Instant.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError validation(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ApiError("validation_failed", "Request validation failed", details, Instant.now());
    }
}
