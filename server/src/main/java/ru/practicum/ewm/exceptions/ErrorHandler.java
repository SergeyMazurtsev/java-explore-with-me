package ru.practicum.ewm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exceptions.dto.ApiError;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ApiError> notFoundException(final NotFoundException e) {
        return new ResponseEntity<>(ApiError.builder()
                .errors(Arrays.asList(e.getStackTrace()))
                .message(e.getMessage())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(LocalDateTime.now().toString())
                .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> integrityViolationException(final IntegrityViolationException e) {
        return new ResponseEntity<>(ApiError.builder()
                .errors(Arrays.asList(e.getStackTrace()))
                .message(e.getMessage())
                .reason("Integrity constraint has been violated")
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now().toString())
                .build(),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> badRequestException(final ValidationException e) {
        return new ResponseEntity<>(ApiError.builder()
                .errors(Arrays.asList(e.getStackTrace()))
                .message(e.getMessage())
                .reason("For the requested operation the conditions are not met.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().toString())
                .build(),
                HttpStatus.BAD_REQUEST);
    }
}
