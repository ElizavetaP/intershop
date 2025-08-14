package ru.practicum.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.intershop.model.Order;

public interface OrderRepository  extends JpaRepository<Order, Long> {
}
