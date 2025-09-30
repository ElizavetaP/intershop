package ru.practicum.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.dto.CacheablePageDto;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemDtoService {

    @Autowired
    private CachedItemService cachedItemService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    public Flux<ItemDto> getAllItemsWithCart(String username) {
        return Mono.zip(
                cachedItemService.getAllItems().collectList(),    // Все товары
                username != null 
                    ? cartService.getAllNewCartItem(username).collectList()  // Корзина авторизованного из БД
                    : Mono.just(List.<CartItem>of())  // Для анонимного пользователя создаем пустой список
        ).flatMapMany(tuple -> {
            List<Item> items = tuple.getT1();
            List<CartItem> cartItems = tuple.getT2();
            return Flux.fromIterable(getListItemDto(items, cartItems));
        });
    }

    public Mono<Page<ItemDto>> getItemsWithCart(String search, String sort, int pageNumber, int pageSize, String username) {
        return getItemsWithCartCached(search, sort, pageNumber, pageSize, username)
                .map(CacheablePageDto::toPage);
    }

    @Cacheable(value = "itemsWithCart", key = "#search + '_' + #sort + '_' + #pageNumber + '_' + #pageSize + '_' + (#username != null ? #username : 'anonymous')")  
    public Mono<CacheablePageDto<ItemDto>> getItemsWithCartCached(String search, String sort, int pageNumber, int pageSize, String username) {
        log.debug("Загрузка товаров с корзиной из БД: search={}, sort={}, page={}, size={}, user={}", 
                search, sort, pageNumber, pageSize, username != null ? username : "anonymous");
        
        return itemService.getItemsWithSearch(search, sort, pageNumber, pageSize)
                .flatMap(itemsPage -> {
                    Mono<List<CartItem>> cartItemsMono = username != null
                        ? cartService.getAllNewCartItem(username).collectList()  // Корзина авторизованного из БД
                        : Mono.just(List.<CartItem>of());  // Пустой список для анонимных
                    
                    return cartItemsMono.map(cartItems -> {
                        List<ItemDto> itemDtos = getListItemDto(itemsPage.getContent(), cartItems);
                        Page<ItemDto> page = new PageImpl<>(itemDtos, itemsPage.getPageable(), itemsPage.getTotalElements());
                        return new CacheablePageDto<>(page);
                    });
                });
    }

    public List<ItemDto> getListItemDto(List<Item> items, List<CartItem> cartItems) {
        Map<Long, CartItem> cartItemMap = cartItems.stream()
                .collect(Collectors.toMap(cartItem -> cartItem.getItemId(), Function.identity()));

        return items.stream()
                .map(item -> new ItemDto(item, Optional.ofNullable(cartItemMap.get(item.getId()))))
                .collect(Collectors.toList());
    }

    public Mono<ItemDto> getItemDto(Long itemId, String username) {
        return Mono.zip(
                cachedItemService.getItemById(itemId),
                username != null
                    ? cartService.getNewCartItemByItemId(itemId, username)
                        .map(Optional::of)
                        .defaultIfEmpty(Optional.<CartItem>empty())
                    : Mono.just(Optional.<CartItem>empty())  // Пустой Optional для анонимных
        ).map(tuple -> {
            Item item = tuple.getT1();
            Optional<CartItem> optionalCartItem = tuple.getT2();
            return new ItemDto(item, optionalCartItem);
        });
    }

}
