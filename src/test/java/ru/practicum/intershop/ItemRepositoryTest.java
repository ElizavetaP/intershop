package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.repository.ItemRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByTitleOrDescription_ShouldFindByTitleAndCaseInsensitive() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByTitleOrDescription("КЕПКА", pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Кепка синяя");
    }

    @Test
    void findByTitleOrDescription_ShouldReturnEmptyForNoMatches() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByTitleOrDescription("несуществующий", pageRequest);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

}