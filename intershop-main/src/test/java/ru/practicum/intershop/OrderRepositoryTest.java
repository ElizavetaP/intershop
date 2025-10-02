package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.practicum.intershop.model.Order;
import ru.practicum.intershop.repository.OrderRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findById_ShouldReturnOrderWhenExists() {
        StepVerifier.create(orderRepository.findById(1L))
                .assertNext(order -> {
                    assertThat(order).isNotNull();
                    assertThat(order.getId()).isEqualTo(1L);
                    assertThat(order.getCreatedAt()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnEmptyForNonExistentId() {
        StepVerifier.create(orderRepository.findById(999L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        StepVerifier.create(orderRepository.findAll().collectList())
                .assertNext(orders -> {
                    assertThat(orders).isNotNull();
                    assertThat(orders.size()).isGreaterThanOrEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void save_ShouldPersistNewOrder() {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setUsername("user");

        StepVerifier.create(orderRepository.save(order))
                .assertNext(savedOrder -> {
                    assertThat(savedOrder).isNotNull();
                    assertThat(savedOrder.getId()).isNotNull();
                    assertThat(savedOrder.getCreatedAt()).isNotNull();
                })
                .verifyComplete();
    }

}