package com.inventory.server.item.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemUpdateData(
                             @Size(max = 30, min = 1)
                             String itemName,

                             @Size(max = 50)
                             String description,

                             @Positive
                             @DecimalMax("99999999.99")
                             BigDecimal price,

                             @Min(value = 0)
                             Integer numberInStock) {
}
