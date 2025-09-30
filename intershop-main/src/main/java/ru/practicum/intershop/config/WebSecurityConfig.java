package ru.practicum.intershop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Отключение CSRF-защиты
                .csrf(csrf -> csrf.disable())

                .authorizeExchange(exchanges -> exchanges
                        // Публичные страницы
                        .pathMatchers("/", "/main/**", "/images/**").permitAll()
                        .pathMatchers("/logout").permitAll()
                        
                        // Защищенные страницы
                        .pathMatchers("/login", "/cart/**", "/orders/**").authenticated()
                        
                        // Все остальные требуют аутентификации
                        .anyExchange().authenticated()
                )
                
                // HTTP Basic Authentication для авторизации пользователей
                .httpBasic(basic -> {})
                
                .build();
    }

    /**
     * Кодировщик паролей BCrypt для проверки хешей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
