package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.ItemDtoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private ItemDtoService itemDtoService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String redirectToItems() {
        return "redirect:/main/items";
    }

    @GetMapping("/main/items")
    public String getItems(
            @RequestParam(defaultValue = "") String search,  // строка с поисков по названию/описанию товара
            @RequestParam(defaultValue = "NO") String sort,  // сортировка перечисление NO, ALPHA, PRICE
            @RequestParam(defaultValue = "10") int pageSize,  // максимальное число товаров на странице
            @RequestParam(defaultValue = "1") int pageNumber,  // номер текущей страницы
            Model model) {

        List<ItemDto> itemDtos = itemDtoService.getAllItemsWithCart();

        // Разбиение на строки по N товаров
        List<List<ItemDto>> itemsInRows = chunkItems(itemDtos, 3);  // 3 товара в ряду

        Map<String, Object> pagingData = new HashMap<>();
        pagingData.put("pageNumber", 1);  // Номер текущей страницы (с 1)
        pagingData.put("pageSize", itemDtos.size());  // Размер страницы
        pagingData.put("hasNext", false);  // Есть ли следующая страница
        pagingData.put("hasPrevious", false);  // Есть ли предыдущая страница

        model.addAttribute("items", itemsInRows);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", pagingData);

        return "main";
    }

    // Метод для разделения списка на строки по N элементов
    private List<List<ItemDto>> chunkItems(List<ItemDto> items, int chunkSize) {
        List<List<ItemDto>> chunks = new ArrayList<>();
        for (int i = 0; i < items.size(); i += chunkSize) {
            chunks.add(items.subList(i, Math.min(i + chunkSize, items.size())));
        }
        return chunks;
    }

    @PostMapping("/main/items/{id}")
    public String changeCountOfItem(@PathVariable("id") Long id,
                                    @RequestParam("action") String action,
                                    @RequestParam(value = "count") Integer count){
        cartService.changeCountOfItem(id, action, count);
        return "redirect:/main/items";
    }

}
