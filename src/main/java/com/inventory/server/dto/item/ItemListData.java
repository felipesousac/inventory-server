package com.inventory.server.dto.item;

import com.inventory.server.dto.category.CategoryListData;

import java.math.BigDecimal;

public record ItemListData(Long id,
                           String itemName,
                           CategoryListData category,
                           String description,
                           BigDecimal price,
                           Integer numberInStock) {
}
