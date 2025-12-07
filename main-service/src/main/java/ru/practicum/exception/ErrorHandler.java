package ru.practicum.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)

    public ResponseEntity<Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiError(
                        "METHOD_NOT_ALLOWED",
                        "The requested method is not allowed for the resource.",
                        e.getMessage())
                );
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleNotFound(final NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND",
                        "Объект не найден",
                        e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAlreadyExistsException(final AlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        "CONFLICT",
                        "Integrity constraint has been violated.",
                        e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleRequestAccessException(final RequestAccessException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        "CONFLICT",
                        "For the requested operation the conditions are not met.",
                        e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleParticipationLimitReachedException(final ParticipationLimitReachedException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        "CONFLICT",
                        "For the requested operation the conditions are not met.",
                        e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleEventNotPublishedException(final EventNotPublishedException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        "CONFLICT",
                        "For the requested operation the conditions are not met.",
                        e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleEventStateException(final EventStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        "CONFLICT",
                        "For the requested operation the conditions are not met.",
                        e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleEventDateTooSoon(final EventDateTooSoonException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        "CONFLICT",
                        "For the requested operation the conditions are not met.",
                        e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidation(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        "BAD_REQUEST",
                        "Incorrectly made request.",
                        e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        "BAD_REQUEST",
                        "Incorrectly made request.",
                        "Parameter '" + e.getParameterName() + "' is required but was not provided."));
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        if (fieldError != null) {
            String message = "field: " + fieldError.getField() + ". " + fieldError.getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(
                            "BAD_REQUEST",
                            "Incorrectly made request.",
                            message));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(
                            "BAD_REQUEST",
                            "Incorrectly made request.",
                            "нарушены правила валидации"));
        }
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleBadJson(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        "BAD_REQUEST",
                        "Некорректный JSON",
                        e.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        "BAD_REQUEST",
                        "Некорректный запрос в параметре " + e.getName(),
                        e.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred.",
                        e.getMessage()));
    }
}
