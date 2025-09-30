package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;
import ru.practicum.intershop.dto.ItemActionDto;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.ItemDtoService;

import java.security.Principal;

@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemDtoService itemDtoService;

    @Autowired
    CartService cartService;

    @GetMapping("/{id}")
    public Mono<String> getCartItem(@PathVariable(name = "id") Long id,
                                    Principal principal,
                                    Model model) {

        boolean isAuthenticated = principal != null;
        String username = isAuthenticated ? principal.getName() : null;
        
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        return itemDtoService.getItemDto(id, username)
                .map(itemDto -> {
                    model.addAttribute("item", itemDto);
                    return "item";
                });
    }

    @PostMapping("/{id}")
    public Mono<String> changeCountOfItem(@PathVariable("id") Long id,
                                          @Valid @ModelAttribute ItemActionDto itemActionDto,
                                          Principal principal) {
        return cartService.changeCountOfItemByItemId(id, itemActionDto.getAction(), itemActionDto.getCount(), principal.getName())
                .then(Mono.just("redirect:/items/" + id));
    }
}
