package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.intershop.exception.ItemNotFoundException;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.service.ItemService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Test
    void getAllItems_ShouldReturnAllItems() {
        List<Item> result = itemService.getAllItems()
                .collectList()
                .block();

        assertThat(result).hasSize(7);
        assertThat(result).extracting(Item::getTitle)
                .contains("Кепка", "Шапка", "Куртка", "Шарф", "Рубашка", "Футболка", "Майка");
    }

    @Test
    void getItemById_ShouldReturnCorrectItem() {
        Item result = itemService.getItemById(1L)
                .block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Кепка");
        assertThat(result.getDescription()).isEqualTo("Кепка синяя");
        assertThat(result.getPrice()).isEqualTo(1000);
    }

    @Test
    void getItemById_ShouldThrowExceptionIfNotFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemById(999L).block();
        });
    }
}