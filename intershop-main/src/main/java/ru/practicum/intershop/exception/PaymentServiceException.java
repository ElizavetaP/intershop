package ru.practicum.intershop.exception;

/**
 * Исключение для ошибок платежного сервиса.
 * Выбрасывается когда платежный сервис возвращает некорректные ответы
 * или работает неправильно.
 */
public class PaymentServiceException extends RuntimeException {
    
    public PaymentServiceException(String message) {
        super(message);
    }
    
    public PaymentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
