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
    @Query("UPDATE cart_item SET quantity = quantity + 1 WHERE id = :id AND orders_id IS NULL")
    Mono<Integer> incrementCountById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE cart_item SET quantity = quantity - 1 WHERE id = :id AND orders_id IS NULL")
    Mono<Integer> decrementCountById(@Param("id") Long id);

    // Метод для получения одного CartItem по itemId, где orders_id == null (заказ еще не создан)
    @Query("SELECT * FROM cart_item WHERE item_id = :itemId AND orders_id IS NULL")
    Mono<CartItem> getCartItemByItemIdAndOrderIsNull(@Param("itemId") Long itemId);

    @Query("SELECT * FROM cart_item WHERE orders_id IS NULL")
    Flux<CartItem> getAllNewCartItem();

    // Дополнительные методы для работы с заказами
    @Query("SELECT * FROM cart_item WHERE orders_id = :orderId")
    Flux<CartItem> findByOrdersId(@Param("orderId") Long orderId);

}
