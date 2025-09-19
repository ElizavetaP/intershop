package ru.practicum.intershop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.practicum.intershop.payment.client.api.BalanceApi;
import ru.practicum.intershop.payment.client.api.PaymentApi;
import ru.practicum.intershop.payment.client.model.BalanceResponse;
import ru.practicum.intershop.payment.client.model.PaymentRequest;
import ru.practicum.intershop.payment.client.model.PaymentResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentClientServiceTest {

    @Mock
    private BalanceApi balanceApi;

    @Mock
    private PaymentApi paymentApi;

    private PaymentClientService paymentClientService;

    @BeforeEach
    void setUp() {
        paymentClientService = new PaymentClientService(balanceApi, paymentApi);
    }

    @Test
    @DisplayName("Должен успешно получить баланс")
    void shouldSuccessfullyGetBalance() {
        Long expectedBalance = 15000L;
        BalanceResponse balanceResponse = new BalanceResponse().balance(expectedBalance);
        ResponseEntity<BalanceResponse> responseEntity = ResponseEntity.ok(balanceResponse);

        when(balanceApi.getBalanceWithHttpInfo()).thenReturn(Mono.just(responseEntity));

        StepVerifier.create(paymentClientService.getCurrentBalance())
                .expectNext(expectedBalance)
                .verifyComplete();
    }

    @Test
    void shouldHandleBalanceServiceError() {
        when(balanceApi.getBalanceWithHttpInfo())
                .thenReturn(Mono.error(new RuntimeException("Сервис недоступен")));

        StepVerifier.create(paymentClientService.getCurrentBalance())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldSuccessfullyProcessPayment() {
        Long amount = 5000L;
        Long orderId = 123L;
        String transactionId = "txn_12345";

        PaymentResponse paymentResponse = new PaymentResponse()
                .success(true)
                .transactionId(transactionId);
        ResponseEntity<PaymentResponse> responseEntity = ResponseEntity.ok(paymentResponse);

        when(paymentApi.processPaymentWithHttpInfo(any(PaymentRequest.class)))
                .thenReturn(Mono.just(responseEntity));

        StepVerifier.create(paymentClientService.processPayment(amount, orderId))
                .assertNext(result -> {
                    assertTrue(result.isSuccess(), "Платеж должен быть успешным");
                    assertEquals(transactionId, result.getTransactionId(), "ID транзакции должен совпадать");
                    assertNull(result.getErrorMessage(), "Сообщение об ошибке должно быть null");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleUnsuccessfulPayment() {
        Long amount = 15000L; // Больше баланса
        Long orderId = 456L;

        PaymentResponse paymentResponse = new PaymentResponse()
                .success(false)
                .transactionId(null);
        ResponseEntity<PaymentResponse> responseEntity = ResponseEntity.ok(paymentResponse);

        when(paymentApi.processPaymentWithHttpInfo(any(PaymentRequest.class)))
                .thenReturn(Mono.just(responseEntity));

        StepVerifier.create(paymentClientService.processPayment(amount, orderId))
                .assertNext(result -> {
                    assertFalse(result.isSuccess(), "Платеж должен быть неуспешным");
                    assertNull(result.getTransactionId(), "ID транзакции должен быть null");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandlePaymentServiceError() {
        Long amount = 1000L;
        Long orderId = 999L;

        when(paymentApi.processPaymentWithHttpInfo(any(PaymentRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Платежный сервис недоступен")));

        StepVerifier.create(paymentClientService.processPayment(amount, orderId))
                .expectError(RuntimeException.class)
                .verify();
    }
}
