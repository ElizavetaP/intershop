package ru.practicum.intershop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.intershop.payment.client.ApiClient;
import ru.practicum.intershop.payment.client.api.BalanceApi;
import ru.practicum.intershop.payment.client.api.PaymentApi;

/**
 * Конфигурация клиента для платежного сервиса.
 */
@Configuration
public class PaymentClientConfig {

    @Value("${payment.service.url:http://localhost:8081}")
    private String paymentServiceUrl;

    /**
     * Настройка дефолтного API клиента (устанавливаем url).
     */
    @Bean
    public ApiClient apiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(paymentServiceUrl);
        return apiClient;
    }

    /**
     * Бин для работы с балансом.
     */
    @Bean
    public BalanceApi balanceApi(ApiClient apiClient) {
        return new BalanceApi(apiClient);
    }

    /**
     * Бин для работы с платежами.
     */
    @Bean
    public PaymentApi paymentApi(ApiClient apiClient) {
        return new PaymentApi(apiClient);
    }
}
