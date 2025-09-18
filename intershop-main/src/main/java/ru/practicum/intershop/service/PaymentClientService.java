package ru.practicum.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.exception.PaymentServiceException;
import ru.practicum.intershop.payment.client.api.BalanceApi;
import ru.practicum.intershop.payment.client.api.PaymentApi;
import ru.practicum.intershop.payment.client.model.BalanceResponse;
import ru.practicum.intershop.payment.client.model.PaymentRequest;
import ru.practicum.intershop.payment.client.model.PaymentResponse;

/**
 * Сервис для работы с платежным API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentClientService {

    private final BalanceApi balanceApi;
    private final PaymentApi paymentApi;

    @Value("${payment.service.timeout:5000}")
    private int timeoutMs;

    /**
     * Получить текущий баланс пользователя
     */
    public Mono<Long> getCurrentBalance() {
        log.debug("Запрос баланса в платежный сервис");
        
        return balanceApi.getBalanceWithHttpInfo()
                .map(responseEntity -> {
                    BalanceResponse balance = responseEntity.getBody();
                    if (balance != null) {
                        log.debug("Получен баланс: {}", balance.getBalance());
                        return balance.getBalance();
                    }
                    log.error("Платежный сервис вернул пустой баланс)");
                    throw new PaymentServiceException("Пустой баланс");
                })
                .doOnError(error -> log.error("Ошибка при получении баланса", error));
    }

    /**
     * Обработать платеж
     * 
     * @param amount сумма платежа
     */
    public Mono<PaymentResult> processPayment(Long amount, Long orderId) {
        log.debug("Обработка платежа: сумма={}, заказ={}", amount, orderId);
        
        PaymentRequest request = new PaymentRequest()
                .amount(amount)
                .orderId(orderId);
        
        return paymentApi.processPaymentWithHttpInfo(request)
                .map(responseEntity -> {
                    PaymentResponse response = responseEntity.getBody();
                    if (response != null) {
                        boolean success = Boolean.TRUE.equals(response.getSuccess());
                        log.debug("Результат платежа: success={}, transactionId={}", 
                                success, response.getTransactionId());
                        return new PaymentResult(success, response.getTransactionId(), null);
                    }
                    log.error("Платежный сервис вернул некорректный ответ");
                    throw new PaymentServiceException("Платежный сервис вернул некорректный ответ");
                })
                .doOnError(error -> log.error("Ошибка при обработке платежа", error));
    }

    /**
     * Результат обработки платежа
     */
    public static class PaymentResult {
        private final boolean success;
        private final String transactionId;
        private final String errorMessage;

        public PaymentResult(boolean success, String transactionId, String errorMessage) {
            this.success = success;
            this.transactionId = transactionId;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public String toString() {
            return "PaymentResult{" +
                    "success=" + success +
                    ", transactionId='" + transactionId + '\'' +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}
