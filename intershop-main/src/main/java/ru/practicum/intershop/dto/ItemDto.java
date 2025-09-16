package ru.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.intershop.model.CartItem;
import ru.practicum.intershop.model.Item;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    // Поля из Item
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Integer price;

    // Поле из CartItem
    private Integer count; // Количество в корзине (0 если не добавлен)

    // Конструктор из Item (когда товара нет в корзине)
    public ItemDto(Item item) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imgPath = item.getImgPath();
        this.price = item.getPrice();
        this.count = 0;
    }

    public ItemDto(Item item, Optional<CartItem> optionalCartItem) {
        this(item);
        this.count = optionalCartItem.map(CartItem::getCount).orElse(0);
    }

}
