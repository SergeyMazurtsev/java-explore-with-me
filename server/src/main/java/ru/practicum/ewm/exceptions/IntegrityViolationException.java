package ru.practicum.ewm.exceptions;

public class IntegrityViolationException extends RuntimeException {
    public IntegrityViolationException(String message) {
        super(message);
    }
}
