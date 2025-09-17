package ru.practicum.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.model.Item;

@Slf4j
@Service
@RequiredArgsConstructor
public class CachedItemService {

    private final ItemService itemService;

    /**
     * Получить все товары
     */
    @Cacheable(value = "items", key = "'all'")   //Ключ "items::all"
    public Flux<Item> getAllItems() {
        log.debug("Загрузка всех товаров из БД (будет закешировано)");
        return itemService.getAllItems();
    }

    /**
     * Получить товар по ID
     */
    @Cacheable(value = "items", // Имя кеша и первая часть ключа
            key = "#id")        // Вторая часть ключа
    public Mono<Item> getItemById(Long id) {
        log.debug("Загрузка товара {} из БД (будет закешировано)", id);
        return itemService.getItemById(id);
    }

}
