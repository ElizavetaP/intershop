package ru.practicum.intershop.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.repository.CartItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final String ACTION_INCREASE = "plus";
    private static final String ACTION_DECREASE = "minus";
    private static final String ACTION_DELETE = "delete";

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ItemService itemService;

    public List<CartItem> getAllNewCartItem() {
        return cartItemRepository.getAllNewCartItem();
    }

    public Optional<CartItem> getNewCartItemByItemId(Long itemId) {
        return cartItemRepository.getCartItemByItemIdAndOrderIsNull(itemId);
    }

    @Transactional
    public void performCartAction(Long cartItemId, String action, int currentCount) {
        switch (action) {
            case ACTION_INCREASE -> cartItemRepository.incrementCountById(cartItemId);
            case ACTION_DECREASE -> {
                if (currentCount <= 1) {
                    cartItemRepository.deleteById(cartItemId);
                } else {
                    cartItemRepository.decrementCountById(cartItemId);
                }
            }
            case ACTION_DELETE -> cartItemRepository.deleteById(cartItemId);
        }
    }

    @Transactional
    public void changeCountOfItemByItemId(Long itemId, String action, int currentCount) {
        Optional<CartItem> optionalCartItem = getNewCartItemByItemId(itemId);

        if (action.equals(ACTION_INCREASE) && optionalCartItem.isEmpty()) {
            //прежде чем увеличить count, создаем cartItem
            createCartItem(itemId);
            optionalCartItem = getNewCartItemByItemId(itemId);
        }
        performCartAction(optionalCartItem.get().getId(), action, currentCount);
    }

    public void createCartItem(Long itemId) {
        Item item = itemService.getItemById(itemId);
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setCount(0);
        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    public int getTotalPriceInCart(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToInt(c -> c.getCount() * c.getItem().getPrice())
                .sum();
    }
}
