package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.exception.OrderNotFoundException;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Order;
import ru.practicum.intershop.repository.CartItemRepository;
import ru.practicum.intershop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    public Mono<Order> createOrder(List<CartItem> cartItems) {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    //Обновляем все CartItem в заказе с ID заказа и ценой на момент создания заказа
                    Flux<CartItem> updatedCartItems = Flux.fromIterable(cartItems)
                            .flatMap(cartItem -> {
                                // Устанавливаем ID заказа и цену на момент покупки
                                cartItem.setOrdersId(savedOrder.getId());
                                cartItem.setPrice(cartItem.getPrice());
                                return cartItemRepository.save(cartItem);
                            });

                    return updatedCartItems.collectList()
                            .map(updatedItems -> {
                                savedOrder.setCartItems(updatedItems);
                                return savedOrder;
                            });
                });
    }

    public Flux<Order> getOrders() {
        return orderRepository.findAll()
                .flatMap(this::loadOrderWithCartItems);
    }

    public Mono<Order> getOrder(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(id)))
                .flatMap(this::loadOrderWithCartItems);
    }

    @Autowired
    private ItemService itemService;

    // Загрузка заказа с элементами корзины
    private Mono<Order> loadOrderWithCartItems(Order order) {
        return cartItemRepository.findByOrdersId(order.getId())
                .flatMap(cartItem -> 
                    // Загружаем Item для каждого CartItem
                    itemService.getItemById(cartItem.getItemId())
                        .map(item -> {
                            cartItem.setItem(item); // @Transient поле
                            return cartItem;
                        })
                )
                .collectList()
                .map(cartItems -> {
                    order.setCartItems(cartItems);
                    return order;
                });
    }
}
