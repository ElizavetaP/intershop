package ru.practicum.intershop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Тесты для проверки веб-авторизации через HTTP Basic Auth
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("Тесты веб-авторизации")
class WebSecurityTest {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Проверка доступа к публичному эндпоинту
     */
    @Test
    void shouldAccessPublicEndpoint() {
        webTestClient
                .get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * Проверка запрета доступа к защищённому эндпоинту для неавторизованных пользователей
     */
    @Test
    void shouldDenyAccessToSecureEndpointForAnonymousUser() {
        webTestClient
                .get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * Проверка доступа к защищённому эндпоинту с правильными учётными данными
     */
    @Test
    void shouldAccessSecureEndpointWithValidCredentials() {
        webTestClient
                .get()
                .uri("/cart/items")
                .headers(headers -> headers.setBasicAuth("user", "user"))
                .exchange()
                .expectStatus().isOk();
    }

    /**
     * Проверка отказа при неправильных учётных данных
     */
    @Test
    void shouldDenyAccessWithInvalidCredentials() {
        webTestClient
                .get()
                .uri("/cart/items")
                .headers(headers -> headers.setBasicAuth("user", "wrongpassword"))
                .exchange()
                .expectStatus().isUnauthorized(); // 401
    }

    /**
     * Тест 6: Логин успешен с правильными credentials
     */
    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        webTestClient
                .get()
                .uri("/login")
                .headers(headers -> headers.setBasicAuth("user", "user"))
                .exchange()
                .expectStatus().is3xxRedirection() // Редирект после успешного логина
                .expectHeader().location("/main/items");
    }

}

