package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.service.CartService;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/items")
    public String getCartItems(Model model){
        List<CartItem> cartItems = cartService.getAllNewCartItem();
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("empty", cartItems.isEmpty());
        model.addAttribute("total", cartService.getTotalPriceInCart(cartItems));
        return "cart";
    }
}
