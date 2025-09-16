package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;
import ru.practicum.intershop.exception.ItemNotFoundException;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.service.ItemService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.data-locations=classpath:test-data.sql"
})
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Test
    void getAllItems_ShouldReturnAllItems() {
        StepVerifier.create(itemService.getAllItems().collectList())
                .assertNext(items -> {
                    assertThat(items).hasSize(5);
                    assertThat(items).extracting(Item::getTitle)
                            .contains("Кепка синяя", "Шапка красная", "Куртка зимняя", "Футболка зеленая", "Шарф женский");
                })
                .verifyComplete();
    }

    @Test
    void getItemById_ShouldReturnCorrectItem() {
        StepVerifier.create(itemService.getItemById(1L))
                .assertNext(item -> {
                    assertThat(item).isNotNull();
                    assertThat(item.getId()).isEqualTo(1L);
                    assertThat(item.getTitle()).isEqualTo("Кепка синяя");
                    assertThat(item.getDescription()).isEqualTo("Описание кепки синего цвета для тестов");
                    assertThat(item.getPrice()).isEqualTo(1000);
                })
                .verifyComplete();
    }

    @Test
    void getItemById_ShouldThrowExceptionIfNotFound() {
        StepVerifier.create(itemService.getItemById(999L))
                .expectError(ItemNotFoundException.class)
                .verify();
    }
}