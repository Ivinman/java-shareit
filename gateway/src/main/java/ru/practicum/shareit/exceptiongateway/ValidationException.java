package ru.practicum.shareit.exceptiongateway;

public class ValidationException extends Exception {
    public ValidationException(final String message) {
        super(message);
    }
}