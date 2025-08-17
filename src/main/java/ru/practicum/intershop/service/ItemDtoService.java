package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    public Page<ItemDto> getItemsWithCart(String search, String sort, int pageNumber, int pageSize) {
        // Получаем товары с пагинацией
        Page<Item> itemsPage = getItemsPage(search, sort, pageNumber, pageSize);

        // Получаем товары из корзины
        List<CartItem> cartItems = cartService.getAllNewCartItem();
        List<ItemDto> itemDtos = getListItemDto(itemsPage.getContent(), cartItems);

        // Создаем новую Page с содержимым itemDtos
        return new PageImpl<>(itemDtos, itemsPage.getPageable(), itemsPage.getTotalElements());
    }

    private Page<Item> getItemsPage(String search, String sort, int pageNumber, int pageSize) {
        Pageable pageable = createPageable(sort, pageNumber, pageSize);

        // Если есть поиск
        if (search != null && !search.trim().isEmpty()) {
            return itemService.getItemsWithSearch(search, pageable);
        }

        // Без поиска
        return itemService.getItemsWithPagination(pageable);
    }

    private Pageable createPageable(String sort, int pageNumber, int pageSize) {
        Sort sorting = switch (sort.toUpperCase()) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };

        return PageRequest.of(pageNumber - 1, pageSize, sorting);
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
