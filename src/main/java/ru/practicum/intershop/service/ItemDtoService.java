package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Item;

import java.util.List;
import java.util.Map;
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

        return getItemDto(items, cartItems);
    }

    public List<ItemDto> getItemDto(List<Item> items, List<CartItem> cartItems) {
        Map<Long, Integer> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(cartItem -> cartItem.getItem().getId(), CartItem::getCount));

        return items.stream()
                .map(item -> new ItemDto(item, cartItemMap.getOrDefault(item.getId(), 0)))
                .collect(Collectors.toList());
    }


}
