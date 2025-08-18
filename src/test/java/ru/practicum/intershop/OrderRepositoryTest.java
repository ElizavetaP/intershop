package ru.practicum.intershop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findById_ShouldLoadOrderWithCartItems() {
        // Находим первый заказ (с кепкой и курткой)
        List<Order> allOrders = orderRepository.findAll();
        assertThat(allOrders).hasSize(2);
        
        // Ищем заказ, который содержит кепку и куртку
        Order orderWithCapAndJacket = allOrders.stream()
                .filter(order -> order.getCartItems().size() == 2)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Order with 2 items not found"));

        // Проверяем содержимое заказа
        List<CartItem> cartItems = orderWithCapAndJacket.getCartItems();
        assertThat(cartItems)
                .extracting(cartItem -> cartItem.getItem().getTitle())
                .containsExactlyInAnyOrder("Кепка синяя", "Куртка зимняя");
        
        // Проверяем количество
        CartItem item1 = cartItems.stream()
                .filter(item -> item.getItem().getTitle().equals("Кепка синяя"))
                .findFirst().orElseThrow();
        assertThat(item1.getCount()).isEqualTo(2);

        CartItem item2 = cartItems.stream()
                .filter(item -> item.getItem().getTitle().equals("Куртка зимняя"))
                .findFirst().orElseThrow();
        assertThat(item2.getCount()).isEqualTo(1);
    }

    @Test
    void findById_ShouldReturnEmptyForNonExistentId() {
        Optional<Order> result = orderRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllOrders() {
        List<Order> result = orderRepository.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void save_ShouldPersistNewOrderWithCartItems() {
        // Находим товар по названию, а не по ID
        Item item = entityManager.getEntityManager()
                .createQuery("SELECT i FROM Item i WHERE i.title = 'Куртка зимняя'", Item.class)
                .getSingleResult();
        
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());

        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setCount(1);
        cartItem.setPrice(item.getPrice());
        cartItem.setOrder(order);

        order.setCartItems(List.of(cartItem));

        Order savedOrder = orderRepository.save(order);
        entityManager.flush();

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCreatedAt()).isNotNull();
        assertThat(savedOrder.getCartItems()).hasSize(1);
        assertThat(savedOrder.getCartItems().get(0).getOrder()).isEqualTo(savedOrder);
    }

}