package ru.practicum.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Table("item")
@Getter
@Setter
public class Item {
    @Id
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String title;
    
    private String description;

    @Positive(message = "Цена должна быть положительной")
    private int price;
    
    @Column("img_path")
    private String imgPath;
}
