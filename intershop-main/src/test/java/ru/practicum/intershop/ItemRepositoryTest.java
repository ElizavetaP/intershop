package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.repository.ItemRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.sql.init.data-locations=classpath:test-data.sql"
})
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void shouldFindAllItems() {
        StepVerifier.create(itemRepository.findAll().collectList())
                .assertNext(items -> {
                    assertThat(items).hasSize(5);
                })
                .verifyComplete();
    }

    @Test
    void shouldFindItemById() {
        StepVerifier.create(itemRepository.findById(1L))
                .assertNext(item -> {
                    assertThat(item).isNotNull();
                    assertThat(item.getId()).isEqualTo(1L);
                    assertThat(item.getTitle()).isEqualTo("Кепка синяя");
                    assertThat(item.getPrice()).isEqualTo(1000);
                })
                .verifyComplete();
    }

    @Test
    void findByTitleOrDescription_ShouldFindByTitleAndCaseInsensitive() {
        StepVerifier.create(itemRepository.findByTitleOrDescription("КЕПКА", 10, 0).collectList())
                .assertNext(items -> {
                    assertThat(items).hasSize(1);
                    assertThat(items.get(0).getTitle()).isEqualTo("Кепка синяя");
                })
                .verifyComplete();
    }

    @Test
    void findByTitleOrDescription_ShouldReturnEmptyForNoMatches() {
        StepVerifier.create(itemRepository.findByTitleOrDescription("несуществующий", 10, 0).collectList())
                .assertNext(items -> {
                    assertThat(items).isEmpty();
                })
                .verifyComplete();
    }

}