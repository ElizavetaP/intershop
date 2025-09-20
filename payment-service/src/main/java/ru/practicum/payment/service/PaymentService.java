package ru.practicum.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.payment.exception.InsufficientFundsException;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class PaymentService {
    
    public static final String TXN_PREFIX = "txn_";
    
    // Эмуляция баланса пользователя
    private final AtomicLong currentBalance;
    
    public PaymentService(@Value("${payment.account.initial-balance:10000}") long initialBalance) {
        this.currentBalance = new AtomicLong(initialBalance);
        log.info("Баланс пользователя: {} руб.", initialBalance);
    }
    
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

            String transactionId = TXN_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            
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
