package ru.practicum.intershop.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.model.Item;

public interface ItemRepository extends R2dbcRepository<Item, Long> {

    @Query("SELECT * FROM item WHERE " +
            "LOWER(title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "LIMIT :limit OFFSET :offset")
    Flux<Item> findByTitleOrDescription(
            @Param("search") String search,
            @Param("limit") int limit,
            @Param("offset") long offset
    );

    @Query("SELECT COUNT(*) FROM item WHERE " +
            "LOWER(title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Mono<Long> countByTitleOrDescription(@Param("search") String search);

    // Эффективная пагинация всех товаров (без поиска)
    @Query("SELECT * FROM item LIMIT :limit OFFSET :offset")
    Flux<Item> findAllPaginated(@Param("limit") int limit, @Param("offset") long offset);
}
