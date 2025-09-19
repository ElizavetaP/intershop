package ru.practicum.intershop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemActionDto {
    
    @NotBlank(message = "Action is required")
    private String action;
    
    @NotNull(message = "Count is required")
    @PositiveOrZero(message = "Count must be positive")
    private Integer count;
}
