package ru.practicum.intershop.exception;

public class OrderNotFoundException  extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("The Order with id " + id + " is not found");
    }
}