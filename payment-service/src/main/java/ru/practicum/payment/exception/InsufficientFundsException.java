package ru.practicum.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при недостатке средств на счете.
 * Соответствует HTTP статусу 402 Payment Required.
 */
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED) // 402
public class InsufficientFundsException extends RuntimeException {
    
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
