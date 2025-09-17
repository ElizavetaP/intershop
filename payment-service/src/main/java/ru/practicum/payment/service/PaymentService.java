package ru.practicum.payment.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.payment.exception.InsufficientFundsException;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {
    
    // Эмуляция баланса пользователя
    private final AtomicLong currentBalance = new AtomicLong(10000L);
    
    /**
     * Получить текущий баланс пользователя
     */
    public Mono<Long> getCurrentBalance() {
        return Mono.just(currentBalance.get());
    }
    
    /**
     * Обработать платеж (списать средства с баланса)
     * 
     * @param amount сумма для списания
     */
    public Mono<PaymentResult> processPayment(Long amount, Long orderId) {
        return Mono.fromCallable(() -> {
            // Проверяем корректность данных
            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("Сумма должна быть больше 0");
            }
            
            if (orderId == null || orderId <= 0) {
                throw new IllegalArgumentException("ID заказа обязателен");
            }

            // Пытаемся списать средства. Записываем предыдущий баланс
            long previousBalance = currentBalance.getAndAccumulate(
                amount,
                (balance, withdrawAmount) -> balance < withdrawAmount ? balance : balance - withdrawAmount
            );
            
            // Проверяем, хватило ли средств
            if (previousBalance < amount) {
                throw new InsufficientFundsException(
                    String.format("Недостаточно средств на счете. Баланс: %d, требуется: %d", 
                                previousBalance, amount)
                );
            }

            String transactionId = "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            
            return new PaymentResult(true, transactionId);
        });
    }
    
    /**
     * Результат операции платежа
     */
    public static class PaymentResult {
        private final boolean success;
        private final String transactionId;
        
        public PaymentResult(boolean success, String transactionId) {
            this.success = success;
            this.transactionId = transactionId;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getTransactionId() {
            return transactionId;
        }
    }
}
