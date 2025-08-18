package ru.practicum.intershop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.repository.ItemRepository;
import ru.practicum.intershop.service.ItemService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item testItem;
    private List<Item> testItems;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setTitle("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(1000);
        testItem.setImgPath("test.png");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setTitle("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(2000);
        item2.setImgPath("item2.png");

        testItems = Arrays.asList(testItem, item2);
    }

    @Test
    void getAllItems_ShouldReturnAllItems() {
        when(itemRepository.findAll()).thenReturn(testItems);

        List<Item> result = itemService.getAllItems();

        assertEquals(testItems, result);
        assertEquals(2, result.size());
        verify(itemRepository).findAll();
    }

    @Test
    void getItemById_ShouldReturnCorrectItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        Item result = itemService.getItemById(1L);

        assertEquals(testItem, result);
        assertEquals("Test Item", result.getTitle());
        verify(itemRepository).findById(1L);
    }

    @Test
    void getItemById_ShouldThrowExceptionIfNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            itemService.getItemById(999L);
        });
        verify(itemRepository).findById(999L);
    }

}