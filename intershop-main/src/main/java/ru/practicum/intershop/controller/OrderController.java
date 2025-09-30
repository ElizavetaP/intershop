package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import jakarta.validation.constraints.Positive;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.OrderService;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
@Validated
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @PostMapping("/buy")
    public Mono<String> buy(Principal principal) {
        return cartService.getAllNewCartItem(principal.getName())
                .collectList()
                .flatMap(cartItems -> orderService.processOrder(cartItems, principal.getName()))
                .map(createdOrder -> "redirect:/orders/" + createdOrder.getId() + "?newOrder=true")
                .onErrorReturn("redirect:/cart/items");
    }

    @GetMapping("/")
    public Mono<String> getOrders(Model model, Principal principal) {
        model.addAttribute("currentUser", principal.getName());

        return orderService.getOrders(principal.getName())
                .collectList()
                .map(orders -> {
                    model.addAttribute("orders", orders);
                    return "orders";
                });
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(@PathVariable("id") @Positive(message = "Order ID must be positive") Long id,
                                 @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder,
                                 Model model, Principal principal) {

        model.addAttribute("currentUser", principal.getName());

        return orderService.getOrder(id, principal.getName())
                .map(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                    return "order";
                });
    }

}
