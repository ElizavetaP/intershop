package ru.practicum.intershop.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("The Item with id " + id + " is not found");
    }
}