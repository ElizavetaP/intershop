package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).get();
    }

    public Page<Item> getItemsWithPagination(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Page<Item> getItemsWithSearch(String search, Pageable pageable) {
        return itemRepository.findByTitleOrDescription(search, pageable);
    }

}
