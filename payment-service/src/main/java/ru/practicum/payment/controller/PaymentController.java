package ru.practicum.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import ru.practicum.payment.api.BalanceApi;
import ru.practicum.payment.api.PaymentApi;
import ru.practicum.payment.exception.InsufficientFundsException;
import ru.practicum.payment.model.BalanceResponse;
import ru.practicum.payment.model.PaymentRequest;
import ru.practicum.payment.model.PaymentResponse;
import ru.practicum.payment.service.PaymentService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController implements BalanceApi, PaymentApi {
    
    private final PaymentService paymentService;
    
    /**
     * GET /api/balance - получить текущий баланс
     */
    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(ServerWebExchange exchange) {
        log.info("Запрос получения баланса");
        
        return paymentService.getCurrentBalance()
            .map(balance -> {
                log.info("Текущий баланс: {}", balance);
                
                BalanceResponse response = new BalanceResponse(balance);
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                log.error("Ошибка получения баланса", error);
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
    }
    
    /**
     * POST /api/payment - обработать платеж
     */
    @Override
    public Mono<ResponseEntity<PaymentResponse>> processPayment(
            Mono<PaymentRequest> paymentRequestMono, 
            ServerWebExchange exchange) {
        
        log.info("Запрос обработки платежа");
        
        return paymentRequestMono
            .flatMap(request -> {
                log.info("Обработка платежа: сумма={}, заказ={}", request.getAmount(), request.getOrderId());
                
                return paymentService.processPayment(request.getAmount(), request.getOrderId())
                    .map(result -> {
                        log.info("Платеж обработан успешно: transactionId={}", result.getTransactionId());
                        
                        PaymentResponse response = new PaymentResponse(
                            result.isSuccess(), 
                            result.getTransactionId()
                        );
                        
                        return ResponseEntity.ok(response);
                    });
            })
                   .onErrorResume(InsufficientFundsException.class, error -> {
                log.warn("Недостаточно средств: {}", error.getMessage());
                PaymentResponse response = new PaymentResponse(false, "error_insufficient_funds");
                return Mono.just(ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response));
            })
            .onErrorResume(IllegalArgumentException.class, error -> {
                log.warn("Некорректные данные запроса: {}", error.getMessage());
                PaymentResponse response = new PaymentResponse(false, "error_invalid_data");
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
            })
            .onErrorResume(error -> {
                // Все остальные ошибки (включая NullPointerException)
                log.error("Внутренняя ошибка сервера: {}", error.getClass().getSimpleName(), error);
                PaymentResponse response = new PaymentResponse(false, "error_internal");
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
            });
    }
}
