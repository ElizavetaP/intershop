package ru.practicum.intershop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemActionDto {
    private String action;
    private String count;
}
