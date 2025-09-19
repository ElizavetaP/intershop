package ru.practicum.intershop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.controller.MainController;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.ItemDtoService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(MainController.class)
class MainControllerWebMvcTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ItemDtoService itemDtoService;

    @MockBean
    private CartService cartService;

    private ItemDto testItemDto1;
    private ItemDto testItemDto2;
    private Page<ItemDto> testPage;

    @BeforeEach
    void setUp() {
        testItemDto1 = new ItemDto();
        testItemDto1.setId(1L);
        testItemDto1.setTitle("Кепка синяя");
        testItemDto1.setDescription("Описание 1");
        testItemDto1.setPrice(1000);
        testItemDto1.setImgPath("cap.png");
        testItemDto1.setCount(0);

        testItemDto2 = new ItemDto();
        testItemDto2.setId(2L);
        testItemDto2.setTitle("Шапка красная");
        testItemDto2.setDescription("Описание 2");
        testItemDto2.setPrice(1200);
        testItemDto2.setImgPath("hat.png");
        testItemDto2.setCount(1);

        List<ItemDto> itemDtos = Arrays.asList(testItemDto1, testItemDto2);
        testPage = new PageImpl<>(itemDtos, PageRequest.of(0, 10), 2);
    }

    @Test
    void redirectToItems_ShouldRedirectToMainItems() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/main/items");
    }

    @Test
    void getItems_ShouldReturnMainPageWithDefaultParameters() {
        when(itemDtoService.getItemsWithCart("", "NO", 1, 10)).thenReturn(Mono.just(testPage));

        webTestClient.get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    // Проверяем, что возвращается не пустая HTML страница
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Витрина товаров")); // Проверяем наличие элементов страницы
                });

        verify(itemDtoService).getItemsWithCart("", "NO", 1, 10);
    }

    @Test
    void getItems_ShouldHandleRequestParametersCorrectly() {
        when(itemDtoService.getItemsWithCart("кепка", "PRICE", 2, 5)).thenReturn(Mono.just(testPage));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", "кепка")
                        .queryParam("sort", "PRICE")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "5")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("кепка")); // Проверяем наличие поискового запроса
                });

        verify(itemDtoService).getItemsWithCart("кепка", "PRICE", 2, 5);
    }

    @Test
    void getItems_WithEmptyResults_ShouldHandleGracefully() {
        Page<ItemDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(itemDtoService.getItemsWithCart("", "NO", 1, 10)).thenReturn(Mono.just(emptyPage));

        webTestClient.get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Витрина товаров")); // Проверяем, что страница загрузилась
                });

        verify(itemDtoService).getItemsWithCart("", "NO", 1, 10);
    }

    @Test
    void changeCountOfItem_PlusAction_ShouldCallServiceAndRedirect() {
        when(cartService.changeCountOfItemByItemId(1L, "plus", 2)).thenReturn(Mono.empty());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "plus");
        formData.add("count", "2");

        webTestClient.post()
                .uri("/main/items/1")
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/main/items");

        verify(cartService).changeCountOfItemByItemId(1L, "plus", 2);
    }

    @Test
    void changeCountOfItem_MinusAction_ShouldCallServiceAndRedirect() {
        when(cartService.changeCountOfItemByItemId(5L, "minus", 1)).thenReturn(Mono.empty());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "minus");
        formData.add("count", "1");

        webTestClient.post()
                .uri("/main/items/5")
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/main/items");

        verify(cartService).changeCountOfItemByItemId(5L, "minus", 1);
    }

    @Test
    void changeCountOfItem_DeleteAction_ShouldCallServiceAndRedirect() {
        when(cartService.changeCountOfItemByItemId(3L, "delete", 5)).thenReturn(Mono.empty());

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "delete");
        formData.add("count", "5");

        webTestClient.post()
                .uri("/main/items/3")
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/main/items");

        verify(cartService).changeCountOfItemByItemId(3L, "delete", 5);
    }

    @Test
    void changeCountOfItem_MissingActionParameter_ShouldReturnError() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("count", "2");

        webTestClient.post()
                .uri("/main/items/1")
                .bodyValue(formData)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(cartService, never()).changeCountOfItemByItemId(anyLong(), anyString(), anyInt());
    }

    @Test
    void changeCountOfItem_MissingCountParameter_ShouldReturnError() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "plus");

        webTestClient.post()
                .uri("/main/items/1")
                .bodyValue(formData)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(cartService, never()).changeCountOfItemByItemId(anyLong(), anyString(), anyInt());
    }


    @Test
    void getItems_WithSearch_ShouldPassCorrectParameters() {
        when(itemDtoService.getItemsWithCart("кепка", "NO", 1, 10)).thenReturn(Mono.just(testPage));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", "кепка")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("кепка"));
                });

        verify(itemDtoService).getItemsWithCart("кепка", "NO", 1, 10);
    }

}