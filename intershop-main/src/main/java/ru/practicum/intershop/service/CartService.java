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
    CachedItemService cachedItemService;

    public Flux<CartItem> getAllNewCartItem(String username) {
        return cartItemRepository.getAllNewCartItem(username)
                .flatMap(this::loadCartItemWithItem);
    }

    public Mono<CartItem> getNewCartItemByItemId(Long itemId, String username) {
        return cartItemRepository.getCartItemByItemIdAndOrderIsNull(itemId, username)
                .flatMap(this::loadCartItemWithItem);
    }

    @Transactional
    public Mono<Void> performCartAction(Long cartItemId, String action, int currentCount, String username) {
        return switch (action) {
            case ACTION_INCREASE -> cartItemRepository.incrementCountById(cartItemId, username).then();
            case ACTION_DECREASE -> {
                if (currentCount <= 1) {
                    yield cartItemRepository.deleteById(cartItemId);
                } else {
                    yield cartItemRepository.decrementCountById(cartItemId, username).then();
                }
            }
            case ACTION_DELETE -> cartItemRepository.deleteById(cartItemId);
            default -> Mono.empty();
        };
    }

    @Transactional
    public Mono<Void> changeCountOfItemByItemId(Long itemId, String action, int currentCount, String username) {
        return getNewCartItemByItemId(itemId, username)
                .switchIfEmpty(
                        //прежде чем увеличить count, создаем cartItem, если он отсутствует в корзине.
                        action.equals(ACTION_INCREASE) ?
                                createCartItem(itemId, username) :
                                Mono.empty()
                )
                .flatMap(cartItem -> performCartAction(cartItem.getId(), action, currentCount, username));
    }

    public Mono<CartItem> createCartItem(Long itemId, String username) {
        return cachedItemService.getItemById(itemId)
                .flatMap(item -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setItemId(itemId);
                    cartItem.setUsername(username);
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
        return cachedItemService.getItemById(cartItem.getItemId())
                .map(item -> {
                    cartItem.setItem(item);
                    cartItem.setPrice(item.getPrice());
                    return cartItem;
                });
    }
}
