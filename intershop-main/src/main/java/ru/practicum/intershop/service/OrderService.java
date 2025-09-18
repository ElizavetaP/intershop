package ru.practicum.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.intershop.exception.OrderNotFoundException;
import ru.practicum.intershop.exception.PaymentFailedException;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Order;
import ru.practicum.intershop.repository.CartItemRepository;
import ru.practicum.intershop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    PaymentClientService paymentClientService;

    public Mono<Order> processOrder(List<CartItem> cartItems) {
        long totalAmount = calculateTotalAmount(cartItems);
        log.debug("Обработка заказа на сумму: {}", totalAmount);

        // проверяем платеж с временным ID
        long tempOrderId = System.currentTimeMillis();
        
        return paymentClientService.processPayment(totalAmount, tempOrderId)
                .flatMap(paymentResult -> {
                    if (paymentResult.isSuccess()) {
                        log.info("Платеж успешен для временного заказа {}: transactionId={}", 
                                tempOrderId, paymentResult.getTransactionId());
                        
                        // Платеж прошел - создаем полный заказ
                        return createOrder(cartItems);
                    } else {
                        log.warn("Платеж отклонен для временного заказа {}: {}", 
                                tempOrderId, paymentResult.getErrorMessage());

                        return Mono.error(new PaymentFailedException(
                                "Платеж отклонен: " + paymentResult.getErrorMessage()));
                    }
                });
    }

    /**
     * Создает полный заказ с товарами после успешного платежа
     */
    private Mono<Order> createOrder(List<CartItem> cartItems) {
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        
        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    log.debug("Заказ создан с ID: {} после успешного платежа", savedOrder.getId());
                    // Сохраняем товары с привязкой к заказу
                    return saveCartItemsWithOrder(savedOrder, cartItems)
                            .collectList()
                            .map(updatedItems -> {
                                savedOrder.setCartItems(updatedItems);
                                return savedOrder;
                            });
                });
    }

    /**
     * Вычисляет общую стоимость заказа
     */
    private long calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToLong(item -> item.getPrice() * item.getCount())
                .sum();
    }

    /**
     * Привязывает товары корзины к заказу и сохраняет их в БД
     */
    private Flux<CartItem> saveCartItemsWithOrder(Order savedOrder, List<CartItem> cartItems) {
        return Flux.fromIterable(cartItems)
                .flatMap(cartItem -> {
                    //Обновляем все CartItem в заказе с ID заказа и ценой на момент создания заказа
                    cartItem.setOrdersId(savedOrder.getId());
                    cartItem.setPrice(cartItem.getPrice());
                    return cartItemRepository.save(cartItem);
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
    private CachedItemService cachedItemService;

    // Загрузка заказа с элементами корзины
    private Mono<Order> loadOrderWithCartItems(Order order) {
        return cartItemRepository.findByOrdersId(order.getId())
                .flatMap(cartItem -> 
                    // Загружаем Item для каждого CartItem
                        cachedItemService.getItemById(cartItem.getItemId())
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
