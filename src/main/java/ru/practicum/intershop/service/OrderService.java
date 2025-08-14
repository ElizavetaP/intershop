package ru.practicum.intershop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Order;
import ru.practicum.intershop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
            cartItem.setPrice(cartItem.getItem().getPrice());
        }
        return orderRepository.save(order);
    }

    public List<Order> getOrders(){
        return orderRepository.findAll();
    }

    public Optional<Order> getOrder(Long id){
        return orderRepository.findById(id);
    }
}
