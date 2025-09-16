package ru.practicum.intershop.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.model.Item;

public interface ItemRepository extends R2dbcRepository<Item, Long> {

    // Поиск с сортировкой по названию
    @Query("SELECT * FROM item WHERE " +
            "LOWER(title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY title ASC " +
            "LIMIT :limit OFFSET :offset")
    Flux<Item> findByTitleOrDescriptionOrderByTitleAsc(
            @Param("search") String search,
            @Param("limit") int limit,
            @Param("offset") long offset
    );

    // Поиск с сортировкой по цене
    @Query("SELECT * FROM item WHERE " +
            "LOWER(title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(description) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY price ASC " +
            "LIMIT :limit OFFSET :offset")
    Flux<Item> findByTitleOrDescriptionOrderByPriceAsc(
            @Param("search") String search,
            @Param("limit") int limit,
            @Param("offset") long offset
    );

    // Поиск без сортировки
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

    // Все товары с сортировкой по названию
    @Query("SELECT * FROM item ORDER BY title ASC LIMIT :limit OFFSET :offset")
    Flux<Item> findAllPaginatedOrderByTitleAsc(@Param("limit") int limit, @Param("offset") long offset);

    // Все товары с сортировкой по цене
    @Query("SELECT * FROM item ORDER BY price ASC LIMIT :limit OFFSET :offset")
    Flux<Item> findAllPaginatedOrderByPriceAsc(@Param("limit") int limit, @Param("offset") long offset);

    // Все товары без сортировки
    @Query("SELECT * FROM item LIMIT :limit OFFSET :offset")
    Flux<Item> findAllPaginated(@Param("limit") int limit, @Param("offset") long offset);
}
