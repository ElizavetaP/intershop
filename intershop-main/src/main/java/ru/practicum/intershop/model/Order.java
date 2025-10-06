package ru.practicum.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Table("orders")
@Getter
@Setter
public class Order {
    @Id
    private Long id;

    @Column("username")
    private String username;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Transient
    private List<CartItem> cartItems;

    public int getTotalSum() {
        return cartItems.stream()
                .mapToInt(cartItem -> cartItem.getPrice() * cartItem.getCount())
                .sum();
    }
}
