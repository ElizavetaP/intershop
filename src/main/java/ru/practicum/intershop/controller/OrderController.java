package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.intershop.exception.OrderNotFoundException;
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
        Order createdOrder = orderService.createOrder(cartItems);
        return "redirect:/orders/" + createdOrder.getId() + "?newOrder=true";
    }

    @GetMapping("/")
    public String getOrders(Model model) {
        List<Order> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable("id") Long id,
                           @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder,
                           Model model) {
        Order order = orderService.getOrder(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

}
