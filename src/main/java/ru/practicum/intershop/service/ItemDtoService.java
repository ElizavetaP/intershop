package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemDtoService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    public List<ItemDto> getAllItemsWithCart() {
        List<Item> items = itemService.getAllItems();
        List<CartItem> cartItems = cartService.getAllNewCartItem();

        return getListItemDto(items, cartItems);
    }

    public List<ItemDto> getListItemDto(List<Item> items, List<CartItem> cartItems) {
        Map<Long, CartItem> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(cartItem -> cartItem.getItem().getId(), Function.identity()));

        return items.stream()
                .map(item -> new ItemDto(item, Optional.ofNullable(cartItemMap.get(item.getId()))))
                .collect(Collectors.toList());
    }

    public ItemDto getItemDto(Long itemId){
        Item item = itemService.getItemById(itemId);
        Optional<CartItem> optionalCartItem = cartService.getNewCartItemByItemId(itemId);
        return new ItemDto(item, optionalCartItem);
    }

}
