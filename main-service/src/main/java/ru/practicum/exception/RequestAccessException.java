package ru.practicum.exception;

public class RequestAccessException extends RuntimeException {
    public RequestAccessException(String message) {
        super(message);
    }
}
