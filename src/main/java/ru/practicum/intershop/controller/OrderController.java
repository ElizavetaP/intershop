package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Order;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @PostMapping("/buy")
    public String buy() {
        List<CartItem> cartItems = cartService.getAllNewCartItem();
        orderService.createOrder(cartItems);
       // return "redirect:/orders/{id}?newOrder=true";
        return "redirect:/";
    }

    @GetMapping("/")
    public String getOrders(Model model) {
        List<Order> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

}
