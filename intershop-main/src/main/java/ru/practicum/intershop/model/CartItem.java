package ru.practicum.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

@Table("cart_item")
@Getter
@Setter
public class CartItem {

    @Id
    private Long id;

    @Column("orders_id")
    private Long ordersId;

    @Column("item_id")
    private Long itemId;

    @Column("username")
    private String username;

    @Column("quantity")
    private int count;

    @Column("price")
    private int price; // Цена на момент покупки

    @Transient
    private Order order;

    @Transient
    private Item item;

}
