package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.repository.ItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByTitleOrDescription_ShouldFindByTitleAndCaseInsensitive() {
        List<Item> result = itemRepository.findByTitleOrDescription("КЕПКА", 10, 0)
                .collectList()
                .block();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Кепка");
    }

    @Test
    void findByTitleOrDescription_ShouldReturnEmptyForNoMatches() {
        List<Item> result = itemRepository.findByTitleOrDescription("несуществующий", 10, 0)
                .collectList()
                .block();

        assertThat(result).isEmpty();
    }

}