package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

    public Flux<ItemDto> getAllItemsWithCart() {
        return Mono.zip(
                itemService.getAllItems().collectList(),    // Все товары
                cartService.getAllNewCartItem().collectList() // Все элементы корзины
        ).flatMapMany(tuple -> {
            List<Item> items = tuple.getT1();
            List<CartItem> cartItems = tuple.getT2();
            return Flux.fromIterable(getListItemDto(items, cartItems));
        });
    }

    public Mono<Page<ItemDto>> getItemsWithCart(String search, String sort, int pageNumber, int pageSize) {
        return getItemsPage(search, sort, pageNumber, pageSize)
                .flatMap(itemsPage -> 
                    cartService.getAllNewCartItem().collectList()
                        .map(cartItems -> {
                            List<ItemDto> itemDtos = getListItemDto(itemsPage.getContent(), cartItems);
                            return new PageImpl<>(itemDtos, itemsPage.getPageable(), itemsPage.getTotalElements());
                        })
                );
    }

    private Mono<Page<Item>> getItemsPage(String search, String sort, int pageNumber, int pageSize) {
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
                .collect(Collectors.toMap(cartItem -> cartItem.getItemId(), Function.identity()));

        return items.stream()
                .map(item -> new ItemDto(item, Optional.ofNullable(cartItemMap.get(item.getId()))))
                .collect(Collectors.toList());
    }

    public Mono<ItemDto> getItemDto(Long itemId) {
        return Mono.zip(
                itemService.getItemById(itemId),
                cartService.getNewCartItemByItemId(itemId)
                    .map(Optional::of)
                    .defaultIfEmpty(Optional.empty())
        ).map(tuple -> {
            Item item = tuple.getT1();
            Optional<CartItem> optionalCartItem = tuple.getT2();
            return new ItemDto(item, optionalCartItem);
        });
    }

}
