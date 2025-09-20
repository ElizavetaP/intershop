package ru.practicum.intershop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.dto.ItemActionDto;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.model.Paging;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.ItemDtoService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@Validated
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
    public Mono<String> getItems(
            @RequestParam(defaultValue = "") String search,  // строка с поиском по названию/описанию товара
            @RequestParam(defaultValue = "NO") String sort,  // сортировка перечисление NO, ALPHA, PRICE
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page number must be greater than 0") int pageNumber,
            Model model) {

        return itemDtoService.getItemsWithCart(search, sort, pageNumber, pageSize)
                .map(itemsPage -> {
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
                });
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
    public Mono<String> changeCountOfItem(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute ItemActionDto itemActionDto) {
        return cartService.changeCountOfItemByItemId(id, itemActionDto.getAction(), itemActionDto.getCount())
                .then(Mono.just("redirect:/main/items"));
    }

}
