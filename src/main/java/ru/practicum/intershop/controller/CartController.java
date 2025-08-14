package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/items")
    public String getCartItems(Model model) {
        List<CartItem> cartItems = cartService.getAllNewCartItem();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("empty", cartItems.isEmpty());
        model.addAttribute("total", cartService.getTotalPriceInCart(cartItems));
        return "cart";
    }

    @PostMapping("/items/{id}")
    public String changeCountOfItem(@PathVariable("id") Long id,
                                    @RequestParam("action") String action,
                                    @RequestParam(value = "count") Integer count) {
        cartService.performCartAction(id, action, count);
        return "redirect:/cart/items";
    }
}
