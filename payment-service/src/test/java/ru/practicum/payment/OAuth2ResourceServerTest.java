package ru.practicum.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Тесты для проверки OAuth2 Resource Server
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("Тесты OAuth2 Resource Server")
class OAuth2ResourceServerTest {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Запрос без аутентификации к /api/v1/balance
     */
    @Test
    void shouldDenyAccessToBalanceWithoutToken() {
        webTestClient
                .get()
                .uri("/api/v1/balance")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * Запрос без аутентификации к /api/v1/payment
     */
    @Test
    void shouldDenyAccessToPaymentWithoutToken() {
        webTestClient
                .post()
                .uri("/api/v1/payment")
                .header("Content-Type", "application/json")
                .bodyValue("""
                        {
                            "amount": 1000,
                            "orderId": 123
                        }
                        """)
                .exchange()
                .expectStatus().isUnauthorized(); // 401
    }

    /**
     * Проверка доступа к /api/v1/balance с моком авторизации
     */
    @Test
    @WithMockUser
    void shouldAllowAccessToBalanceWithToken() {
        webTestClient
                .get()
                .uri("/api/v1/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isNumber();
    }

    /**
     * Проверка доступа к /api/v1/payment с моком авторизации
     */
    @Test
    @WithMockUser
    void shouldAllowAccessToPaymentWithToken() {
        webTestClient
                .post()
                .uri("/api/v1/payment")
                .header("Content-Type", "application/json")
                .bodyValue("""
                        {
                            "amount": 1000,
                            "orderId": 123
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isBoolean()
                .jsonPath("$.transactionId").isNotEmpty();
    }
}

