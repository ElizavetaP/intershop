package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.intershop.dto.ItemDto;
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
    public String getCartItems(@PathVariable(name = "id") Long id,
                               Model model) {
        ItemDto itemDto = itemDtoService.getItemDto(id);
        model.addAttribute("item", itemDto);
        return "item";
    }

    @PostMapping("/{id}")
    public String changeCountOfItem(@PathVariable("id") Long id,
                                    @RequestParam("action") String action,
                                    @RequestParam(value = "count") Integer count) {
        cartService.changeCountOfItemByItemId(id, action, count);
        return "redirect:/items/{id}";
    }
}
