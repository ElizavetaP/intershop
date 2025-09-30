package ru.practicum.intershop.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.model.CartItem;

public interface CartItemRepository extends R2dbcRepository<CartItem, Long> {

    @Modifying
    @Query("UPDATE cart_item SET quantity = quantity + 1 WHERE id = :id AND orders_id IS NULL AND username = :username")
    Mono<Integer> incrementCountById(@Param("id") Long id, @Param("username") String username);

    @Modifying
    @Query("UPDATE cart_item SET quantity = quantity - 1 WHERE id = :id AND orders_id IS NULL AND username = :username")
    Mono<Integer> decrementCountById(@Param("id") Long id, @Param("username") String username);

    // Метод для получения одного CartItem по itemId для конкретного пользователя (корзина)
    @Query("SELECT * FROM cart_item WHERE item_id = :itemId AND orders_id IS NULL AND username = :username")
    Mono<CartItem> getCartItemByItemIdAndOrderIsNull(@Param("itemId") Long itemId, @Param("username") String username);

    // Получить все товары в корзине конкретного пользователя
    @Query("SELECT * FROM cart_item WHERE orders_id IS NULL AND username = :username")
    Flux<CartItem> getAllNewCartItem(@Param("username") String username);

    // Дополнительные методы для работы с заказами
    @Query("SELECT * FROM cart_item WHERE orders_id = :orderId")
    Flux<CartItem> findByOrdersId(@Param("orderId") Long orderId);

}
