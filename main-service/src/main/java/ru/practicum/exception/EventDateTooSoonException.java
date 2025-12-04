package ru.practicum.exception;

public class EventDateTooSoonException extends RuntimeException {
    public EventDateTooSoonException(String message) {
        super(message);
    }
}
