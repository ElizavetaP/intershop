package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.exception.ItemNotFoundException;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Mono<Item> getItemById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new ItemNotFoundException(id)));
    }

    public Mono<Page<Item>> getItemsWithPagination(Pageable pageable) {
        return getItemsWithSearch("", pageable); // Пустой поиск = все товары
    }

    public Mono<Page<Item>> getItemsWithSearch(String search, Pageable pageable) {
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();

        Mono<List<Item>> itemsMono;
        Mono<Long> countMono;

        // Выбираем метод в зависимости от наличия поиска
        if (search.isEmpty()) {
            itemsMono = getAllItemsPaginated(limit, offset);
            countMono = itemRepository.count();
        } else {
            itemsMono = itemRepository
                    .findByTitleOrDescription(search, limit, offset)
                    .collectList();
            countMono = itemRepository.countByTitleOrDescription(search);
        }

        return Mono.zip(itemsMono, countMono)
                .map(tuple -> new PageImpl<>(
                        tuple.getT1(), // List<Item>
                        pageable,      // Pageable
                        tuple.getT2()  // Long totalCount
                ));
    }

    private Mono<List<Item>> getAllItemsPaginated(int limit, long offset) {
        return itemRepository.findAllPaginated(limit, offset)
                .collectList();
    }

}