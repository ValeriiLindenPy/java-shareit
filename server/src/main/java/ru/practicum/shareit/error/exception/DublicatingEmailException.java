package ru.practicum.shareit.error.exception;

public class DublicatingEmailException extends RuntimeException {
    public DublicatingEmailException(String message) {
        super(message);
    }
}
