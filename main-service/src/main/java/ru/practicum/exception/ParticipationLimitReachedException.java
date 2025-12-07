package ru.practicum.exception;

public class ParticipationLimitReachedException extends RuntimeException {
    public ParticipationLimitReachedException(String message) {
        super(message);
    }
}
