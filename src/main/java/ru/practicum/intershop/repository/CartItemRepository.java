package ru.practicum.intershop.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.intershop.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE CartItem c SET c.count = c.count + 1 WHERE c.id = :id AND c.order IS NULL")
    void incrementCountById(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE CartItem c SET c.count = c.count - 1 WHERE c.id = :id AND c.order IS NULL")
    void decrementCountById(Long id);

    // Метод для получения одного CartItem по itemId, где order == null (заказ еще не создан)
    @Query("SELECT c FROM CartItem c WHERE c.item.id = :itemId AND c.order IS NULL")
    Optional<CartItem> getCartItemByItemIdAndOrderIsNull(Long itemId);

    @Query("SELECT c FROM CartItem c WHERE c.order IS NULL")
    List<CartItem> getAllNewCartItem();

}
