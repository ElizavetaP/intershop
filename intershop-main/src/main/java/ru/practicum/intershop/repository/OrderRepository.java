package ru.practicum.intershop.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.model.Order;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
    
    // Найти все заказы конкретного пользователя
    Flux<Order> findByUsername(String username);
    
    // Найти заказ по ID и пользователю
    Mono<Order> findByIdAndUsername(Long id, String username);
}
