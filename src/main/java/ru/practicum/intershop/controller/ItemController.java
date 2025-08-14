package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.intershop.dto.ItemDto;
import ru.practicum.intershop.service.ItemDtoService;

@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemDtoService itemDtoService;

    @GetMapping("/{id}")
    public String getCartItems(@PathVariable(name = "id") Long id,
                               Model model) {
        ItemDto itemDto = itemDtoService.getItemDto(id);
        model.addAttribute("item", itemDto);
        return "item";
    }
}
