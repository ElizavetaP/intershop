package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Order;
import ru.practicum.intershop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public Order createOrder(List<CartItem> cartItems) {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setCartItems(cartItems);

        for (CartItem cartItem : cartItems) {
            cartItem.setOrder(order);
        }
        return orderRepository.save(order);
    }

    public List<Order> getOrders(){
        return orderRepository.findAll();
    }
}
