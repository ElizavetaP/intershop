package ru.practicum.intershop.exception;

/**
 * Исключение для неуспешных платежей.
 * Выбрасывается когда платеж отклонен (недостаточно средств, ошибка обработки и т.д.)
 */
public class PaymentFailedException extends RuntimeException {
    
    public PaymentFailedException(String message) {
        super(message);
    }
    
    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
