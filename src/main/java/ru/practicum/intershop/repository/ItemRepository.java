package ru.practicum.intershop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.intershop.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Поиск с пагинацией
    @Query("SELECT i FROM Item i WHERE " +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Item> findByTitleOrDescription(
            @Param("search") String search,
            Pageable pageable
    );

}
