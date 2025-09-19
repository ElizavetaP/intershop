package ru.practicum.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.service.CachedItemService;
import ru.practicum.intershop.service.ItemService;


import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
class CachedItemServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CachedItemService cachedItemService;

    @MockBean
    private ItemService itemService; // Мокируем базовый сервис

    @Test
    void shouldCacheIndividualItem() {
        Item item = createTestItem(1L, "Тестовый товар", 1500);
        when(itemService.getItemById(1L)).thenReturn(Mono.just(item));

        // первый вызов (загрузка из "БД")
        StepVerifier.create(cachedItemService.getItemById(1L))
                .expectNext(item)
                .verifyComplete();

        // второй вызов (должен взять из кеша)
        StepVerifier.create(cachedItemService.getItemById(1L))
                .expectNext(item)
                .verifyComplete();

        // проверяем, что itemService вызывался только ОДИН раз
        verify(itemService, times(1)).getItemById(1L);
    }

    @Test
    void shouldCacheAllItems() {
        Item item1 = createTestItem(1L, "Товар 1", 1000);
        Item item2 = createTestItem(2L, "Товар 2", 2000);
        when(itemService.getAllItems()).thenReturn(Flux.just(item1, item2));

        // загрузка из "БД"
        StepVerifier.create(cachedItemService.getAllItems())
                .expectNext(item1, item2)
                .verifyComplete();

        //второй вызов (должен взять из кеша)
        StepVerifier.create(cachedItemService.getAllItems())
                .expectNext(item1, item2)
                .verifyComplete();

        // третий вызов (тоже из кеша)
        StepVerifier.create(cachedItemService.getAllItems())
                .expectNext(item1, item2)
                .verifyComplete();

        // проверяем, что itemService вызывался только ОДИН раз
        verify(itemService, times(1)).getAllItems();
        
    }


    private Item createTestItem(Long id, String title, int price) {
        Item item = new Item();
        item.setId(id);
        item.setTitle(title);
        item.setDescription("Описание для " + title);
        item.setPrice(price);
        item.setImgPath("/images/item" + id + ".jpg");
        return item;
    }
}