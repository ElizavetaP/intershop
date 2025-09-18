package ru.practicum.intershop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.intershop.payment.client.ApiClient;

/**
 * Конфигурация клиента для платежного сервиса.
 * BalanceApi и PaymentApi создаются Spring автоматически благодаря @Autowired конструкторам в сгенерированных классах.
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
}
