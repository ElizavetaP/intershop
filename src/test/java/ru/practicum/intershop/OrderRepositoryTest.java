package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.intershop.model.Order;
import ru.practicum.intershop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findById_ShouldReturnOrderWhenExists() {
        Order result = orderRepository.findById(1L)
                .block();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void findById_ShouldReturnEmptyForNonExistentId() {
        Order result = orderRepository.findById(999L)
                .block();

        assertNull(result);
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        List<Order> result = orderRepository.findAll()
                .collectList()
                .block();

        assertNotNull(result);
        assertTrue(result.size() >= 3);
    }

    @Test
    void save_ShouldPersistNewOrder() {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order)
                .block();

        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getId());
        assertNotNull(savedOrder.getCreatedAt());

        Order foundOrder = orderRepository.findById(savedOrder.getId())
                .block();
        
        assertNotNull(foundOrder);
        assertEquals(savedOrder.getId(), foundOrder.getId());
    }

}