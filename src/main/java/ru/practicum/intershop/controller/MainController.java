package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.intershop.model.Item;
import ru.practicum.intershop.service.ItemService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private ItemService itemService;  // Сервис для работы с товарами

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

        List<Item> items = itemService.getAllItems();

        System.out.println(items);

        // Разбиение на строки по N товаров
        List<List<Item>> itemsInRows = chunkItems(items, 3);  // 3 товара в ряду

        Map<String, Object> pagingData = new HashMap<>();
        pagingData.put("pageNumber", 1);  // Номер текущей страницы (с 1)
        pagingData.put("pageSize", items.size());  // Размер страницы
        pagingData.put("hasNext", false);  // Есть ли следующая страница
        pagingData.put("hasPrevious", false);  // Есть ли предыдущая страница

        model.addAttribute("items", itemsInRows);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", pagingData);

        return "main";
    }

    // Метод для разделения списка на строки по N элементов
    private List<List<Item>> chunkItems(List<Item> items, int chunkSize) {
        List<List<Item>> chunks = new ArrayList<>();
        for (int i = 0; i < items.size(); i += chunkSize) {
            chunks.add(items.subList(i, Math.min(i + chunkSize, items.size())));
        }
        return chunks;
    }

}
