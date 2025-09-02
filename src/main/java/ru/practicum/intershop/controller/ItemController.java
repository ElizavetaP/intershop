package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.ItemDtoService;

@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemDtoService itemDtoService;

    @Autowired
    CartService cartService;

    @GetMapping("/{id}")
    public Mono<String> getCartItem(@PathVariable(name = "id") Long id,
                                    Model model) {
        return itemDtoService.getItemDto(id)
                .map(itemDto -> {
                    model.addAttribute("item", itemDto);
                    return "item";
                });
    }

    @PostMapping("/{id}")
    public Mono<String> changeCountOfItem(@PathVariable("id") Long id,
                                          @RequestParam("action") String action,
                                          @RequestParam(value = "count") Integer count) {
        return cartService.changeCountOfItemByItemId(id, action, count)
                .then(Mono.just("redirect:/items/" + id));
    }
}
