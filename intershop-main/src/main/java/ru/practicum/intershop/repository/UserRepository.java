package ru.practicum.intershop.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.model.User;

public interface UserRepository extends R2dbcRepository<User, Long> {

    /**
     * Найти активного пользователя по имени пользователя
     * Используется для Spring Security аутентификации
     */
    Mono<User> findByUsernameAndEnabledTrue(String username);
}