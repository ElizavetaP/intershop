package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @PostMapping("/buy")
    public Mono<String> buy() {
        return cartService.getAllNewCartItem()
                .collectList()
                .flatMap(cartItems -> orderService.processOrder(cartItems))
                .map(createdOrder -> "redirect:/orders/" + createdOrder.getId() + "?newOrder=true")
                .onErrorReturn("redirect:/cart/items");
    }

    @GetMapping("/")
    public Mono<String> getOrders(Model model) {
        return orderService.getOrders()
                .collectList()
                .map(orders -> {
                    model.addAttribute("orders", orders);
                    return "orders";
                });
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(@PathVariable("id") Long id,
                                 @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder,
                                 Model model) {
        return orderService.getOrder(id)
                .map(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                    return "order";
                });
    }

}
