package ru.practicum.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.intershop.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
