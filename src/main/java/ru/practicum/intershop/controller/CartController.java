package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/items")
    public Mono<String> getCartItems(Model model) {
        return cartService.getAllNewCartItem()
                .collectList()
                .map(cartItems -> {
                    model.addAttribute("cartItems", cartItems);
                    model.addAttribute("empty", cartItems.isEmpty());
                    model.addAttribute("total", cartService.getTotalPriceInCart(cartItems));
                    return "cart";
                });
    }

    @PostMapping("/items/{id}")
    public Mono<String> changeCountOfItem(@PathVariable("id") Long id,
                                          @RequestParam("action") String action,
                                          @RequestParam(value = "count") Integer count) {
        return cartService.performCartAction(id, action, count)
                .then(Mono.just("redirect:/cart/items"));
    }
}
