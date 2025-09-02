package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    public Mono<Page<Item>> getItemsWithSearch(String search, String sort, int pageNumber, int pageSize) {
        int limit = pageSize;
        long offset = (long) (pageNumber - 1) * pageSize;

        Mono<List<Item>> itemsMono;
        Mono<Long> countMono;

        // Выбираем метод в зависимости от наличия поиска и сортировки
        if (search.isEmpty()) {
            itemsMono = switch (sort.toUpperCase()) {
                case "ALPHA" -> itemRepository.findAllPaginatedOrderByTitleAsc(limit, offset).collectList();
                case "PRICE" -> itemRepository.findAllPaginatedOrderByPriceAsc(limit, offset).collectList();
                default -> itemRepository.findAllPaginated(limit, offset).collectList();
            };
            countMono = itemRepository.count();
        } else {
            itemsMono = switch (sort.toUpperCase()) {
                case "ALPHA" -> itemRepository.findByTitleOrDescriptionOrderByTitleAsc(search, limit, offset).collectList();
                case "PRICE" -> itemRepository.findByTitleOrDescriptionOrderByPriceAsc(search, limit, offset).collectList();
                default -> itemRepository.findByTitleOrDescription(search, limit, offset).collectList();
            };
            countMono = itemRepository.countByTitleOrDescription(search);
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        
        return Mono.zip(itemsMono, countMono)
                .map(tuple -> new PageImpl<>(
                        tuple.getT1(), // List<Item>
                        pageable,      // Pageable
                        tuple.getT2()  // Long totalCount
                ));
    }

}