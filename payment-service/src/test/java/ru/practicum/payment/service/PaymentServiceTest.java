package ru.practicum.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.practicum.payment.exception.InsufficientFundsException;
import ru.practicum.payment.service.PaymentService.PaymentResult;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaymentService тесты")
class PaymentServiceTest {

    private PaymentService paymentService;
    private static final long INITIAL_BALANCE = 10000L;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(INITIAL_BALANCE);
    }

    @Test
    void shouldReturnCorrectInitialBalance() {
        StepVerifier.create(paymentService.getCurrentBalance())
                .expectNext(INITIAL_BALANCE)
                .verifyComplete();
    }

    @Test
    void shouldSuccessfullyProcessPaymentWithSufficientBalance() {
        Long paymentAmount = 5000L;
        Long orderId = 123L;

        Mono<PaymentResult> result = paymentService.processPayment(paymentAmount, orderId);

        StepVerifier.create(result)
                .assertNext(paymentResult -> {
                    assertTrue(paymentResult.isSuccess(), "Платеж должен быть успешным");
                    assertNotNull(paymentResult.getTransactionId(), "ID транзакции не должен быть null");
                    assertTrue(paymentResult.getTransactionId().startsWith("txn_"), 
                             "ID транзакции должен начинаться с 'txn_'");
                })
                .verifyComplete();

        // Проверяем, что баланс изменился
        StepVerifier.create(paymentService.getCurrentBalance())
                .expectNext(INITIAL_BALANCE - paymentAmount)
                .verifyComplete();
    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenBalanceIsLow() {
        Long paymentAmount = 15000L; // Больше чем баланс
        Long orderId = 456L;

        StepVerifier.create(paymentService.processPayment(paymentAmount, orderId))
                .expectError(InsufficientFundsException.class)
                .verify();

        // Проверяем, что баланс не изменился
        StepVerifier.create(paymentService.getCurrentBalance())
                .expectNext(INITIAL_BALANCE)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionForInvalidAmount() {
        Long orderId = 789L;

        StepVerifier.create(paymentService.processPayment(null, orderId))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(paymentService.processPayment(-100L, orderId))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(paymentService.processPayment(0L, orderId))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldThrowExceptionForInvalidOrderId() {
        Long paymentAmount = 1000L;

        StepVerifier.create(paymentService.processPayment(paymentAmount, null))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(paymentService.processPayment(paymentAmount, -1L))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(paymentService.processPayment(paymentAmount, 0L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void shouldHandleSequentialPayments() {
        Long firstPayment = 3000L;
        Long secondPayment = 2000L;
        Long orderId1 = 100L;
        Long orderId2 = 200L;

        StepVerifier.create(paymentService.processPayment(firstPayment, orderId1))
                .assertNext(result -> assertTrue(result.isSuccess()))
                .verifyComplete();

        // проверяем баланс после первого платежа
        StepVerifier.create(paymentService.getCurrentBalance())
                .expectNext(INITIAL_BALANCE - firstPayment)
                .verifyComplete();

        StepVerifier.create(paymentService.processPayment(secondPayment, orderId2))
                .assertNext(result -> assertTrue(result.isSuccess()))
                .verifyComplete();

        //проверяем итоговый баланс
        StepVerifier.create(paymentService.getCurrentBalance())
                .expectNext(INITIAL_BALANCE - firstPayment - secondPayment)
                .verifyComplete();
    }
}
