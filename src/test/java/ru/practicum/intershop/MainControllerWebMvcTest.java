package ru.practicum.intershop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.intershop.controller.MainController;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.ItemDtoService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
class MainControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

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
    void redirectToItems_ShouldRedirectToMainItems() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    void getItems_ShouldReturnMainPageWithDefaultParameters() throws Exception {
        when(itemDtoService.getItemsWithCart("", "NO", 1, 10)).thenReturn(testPage);

        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attribute("search", ""))
                .andExpect(model().attribute("sort", "NO"));

        verify(itemDtoService).getItemsWithCart("", "NO", 1, 10);
    }

    @Test
    void getItems_ShouldHandleRequestParametersCorrectly() throws Exception {
        when(itemDtoService.getItemsWithCart("кепка", "PRICE", 2, 5)).thenReturn(testPage);

        mockMvc.perform(get("/main/items")
                .param("search", "кепка")
                .param("sort", "PRICE")
                .param("pageNumber", "2")
                .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("search", "кепка"))
                .andExpect(model().attribute("sort", "PRICE"));

        verify(itemDtoService).getItemsWithCart("кепка", "PRICE", 2, 5);
    }

    @Test
    void getItems_WithEmptyResults_ShouldHandleGracefully() throws Exception {
        Page<ItemDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(itemDtoService.getItemsWithCart("", "NO", 1, 10)).thenReturn(emptyPage);

        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"));

        verify(itemDtoService).getItemsWithCart("", "NO", 1, 10);
    }

    @Test
    void changeCountOfItem_PlusAction_ShouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/main/items/1")
                .param("action", "plus")
                .param("count", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(cartService).changeCountOfItemByItemId(1L, "plus", 2);
    }

    @Test
    void changeCountOfItem_MinusAction_ShouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/main/items/5")
                .param("action", "minus")
                .param("count", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(cartService).changeCountOfItemByItemId(5L, "minus", 1);
    }

    @Test
    void changeCountOfItem_DeleteAction_ShouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/main/items/3")
                .param("action", "delete")
                .param("count", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(cartService).changeCountOfItemByItemId(3L, "delete", 5);
    }

    @Test 
    void changeCountOfItem_MissingActionParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/main/items/1")
                .param("count", "2"))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).changeCountOfItemByItemId(anyLong(), anyString(), anyInt());
    }

    @Test
    void changeCountOfItem_MissingCountParameter_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/main/items/1")
                .param("action", "plus"))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).changeCountOfItemByItemId(anyLong(), anyString(), anyInt());
    }


    @Test
    void getItems_WithSearch_ShouldPassCorrectParameters() throws Exception {
        when(itemDtoService.getItemsWithCart("кепка", "NO", 1, 10)).thenReturn(testPage);

        mockMvc.perform(get("/main/items")
                .param("search", "кепка"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("search", "кепка"));

        verify(itemDtoService).getItemsWithCart("кепка", "NO", 1, 10);
    }

}