package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.repository.CartItemRepository;

import java.util.List;

@Service
public class CartService {

    private static final String ACTION_INCREASE = "plus";
    private static final String ACTION_DECREASE = "minus";
    private static final String ACTION_DELETE = "delete";

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ItemService itemService;

    public Flux<CartItem> getAllNewCartItem() {
        return cartItemRepository.getAllNewCartItem()
                .flatMap(this::loadCartItemWithItem);
    }

    public Mono<CartItem> getNewCartItemByItemId(Long itemId) {
        return cartItemRepository.getCartItemByItemIdAndOrderIsNull(itemId)
                .flatMap(this::loadCartItemWithItem);
    }

    @Transactional
    public Mono<Void> performCartAction(Long cartItemId, String action, int currentCount) {
        return switch (action) {
            case ACTION_INCREASE -> cartItemRepository.incrementCountById(cartItemId).then();
            case ACTION_DECREASE -> {
                if (currentCount <= 1) {
                    yield cartItemRepository.deleteById(cartItemId);
                } else {
                    yield cartItemRepository.decrementCountById(cartItemId).then();
                }
            }
            case ACTION_DELETE -> cartItemRepository.deleteById(cartItemId);
            default -> Mono.empty();
        };
    }

    @Transactional
    public Mono<Void> changeCountOfItemByItemId(Long itemId, String action, int currentCount) {
        return getNewCartItemByItemId(itemId)
                .switchIfEmpty(
                    //прежде чем увеличить count, создаем cartItem, если он отсутствует в корзине.
                    action.equals(ACTION_INCREASE) ? 
                        createCartItem(itemId) : 
                        Mono.empty()
                )
                .flatMap(cartItem -> performCartAction(cartItem.getId(), action, currentCount));
    }

    public Mono<CartItem> createCartItem(Long itemId) {
        return itemService.getItemById(itemId)
                .flatMap(item -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setItemId(itemId);
                    cartItem.setItem(item); // @Transient поле
                    cartItem.setCount(0);
                    return cartItemRepository.save(cartItem);
                });
    }

    public Mono<Void> deleteCartItem(CartItem cartItem) {
        return cartItemRepository.delete(cartItem);
    }

    public int getTotalPriceInCart(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToInt(c -> c.getCount() * c.getPrice()) // Используем price из CartItem
                .sum();
    }

    // метод для загрузки Item в CartItem
    private Mono<CartItem> loadCartItemWithItem(CartItem cartItem) {
        return itemService.getItemById(cartItem.getItemId())
                .map(item -> {
                    cartItem.setItem(item);
                    return cartItem;
                });
    }
}
