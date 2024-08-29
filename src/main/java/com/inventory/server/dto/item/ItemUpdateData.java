package com.inventory.server.dto.item;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemUpdateData(@NotBlank
                             @Size(max = 30)
                             String itemName,
                             @NotBlank
                             @Size(max = 50)
                             String description,
                             @NotNull
                             @Positive
                             @DecimalMax("99999999.99")
                             BigDecimal price,
                             @NotNull
                             @Min(value = 0)
                             Integer numberInStock) {
}
