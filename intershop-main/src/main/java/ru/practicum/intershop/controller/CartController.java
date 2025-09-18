package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.service.CartService;
import ru.practicum.intershop.service.PaymentClientService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    PaymentClientService paymentClientService;

    @GetMapping("/items")
    public Mono<String> getCartItems(Model model) {
        return cartService.getAllNewCartItem()
                .collectList()
                .flatMap(cartItems -> {
                    long totalPrice = cartService.getTotalPriceInCart(cartItems);

                    model.addAttribute("cartItems", cartItems);
                    model.addAttribute("empty", cartItems.isEmpty());
                    model.addAttribute("total", totalPrice);
                    
                    // Получаем баланс пользователя
                    return paymentClientService.getCurrentBalance()
                            .map(balance -> {
                                model.addAttribute("balance", balance);
                                model.addAttribute("canBuy", balance >= totalPrice && !cartItems.isEmpty());
                                return "cart";
                            })
                            .onErrorResume(error -> {
                                // Если сервис недоступен - кнопка неактивна
                                model.addAttribute("balance", null);
                                model.addAttribute("canBuy", false);
                                model.addAttribute("paymentError", "Сервис платежей недоступен");
                                return Mono.just("cart");
                            });
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
