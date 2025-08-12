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

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ItemService itemService;

    public List<CartItem> getAllNewCartItem(){
        return cartItemRepository.getAllNewCartItem();
    }

    public Optional<CartItem> getCartItemByItemId(Long itemId){
         return cartItemRepository.getCartItemByItemIdAndOrderIsNull(itemId);
    }

    @Transactional
    public void changeCountOfItem(Long itemId, String action, int count){
        if (action.equals(ACTION_INCREASE)) {
            if (getCartItemByItemId(itemId).isEmpty()){
                createCartItem(itemId);
            }
            cartItemRepository.incrementCountById(itemId);
        } else if (action.equals(ACTION_DECREASE)){
            if(count<=1){
                cartItemRepository.delete(cartItemRepository.getCartItemByItemIdAndOrderIsNull(itemId).get());
            }
            cartItemRepository.decrementCountById(itemId);
        }
    }

    public void createCartItem(Long itemId){
        Item item = itemService.getItemById(itemId);
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setCount(0);
        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(CartItem cartItem){
        cartItemRepository.delete(cartItem);
    }
}
