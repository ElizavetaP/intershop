package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.model.Paging;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.ItemDtoService;

import java.util.ArrayList;
import java.util.List;

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

//        List<ItemDto> itemDtos = itemDtoService.getAllItemsWithCart();
//
//        // Разбиение на строки по N товаров
//        List<List<ItemDto>> itemsInRows = chunkItems(itemDtos, 3);  // 3 товара в ряду

        Page<ItemDto> itemsPage = itemDtoService.getItemsWithCart(search, sort, pageNumber, pageSize);

        // Разбиение на строки по 3 товара (только контент страницы)
        List<List<ItemDto>> itemsInRows = chunkItems(itemsPage.getContent(), 3);


        boolean hasNext = itemsPage.getContent().size() == pageSize;
        boolean hasPrevious = pageNumber > 1;

        Paging paging = new Paging(pageNumber, pageSize, hasNext, hasPrevious);

        model.addAttribute("items", itemsInRows);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);

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
                                    @RequestParam(value = "count") Integer count) {
        cartService.changeCountOfItemByItemId(id, action, count);
        return "redirect:/main/items";
    }

}
