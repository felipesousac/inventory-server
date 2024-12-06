package com.inventory.server.item.dto;

import com.inventory.server.category.dto.CategoryListData;

import java.math.BigDecimal;

public record ItemListData(Long id,
                           String itemName,
                           CategoryListData category,
                           String description,
                           BigDecimal price,
                           Integer numberInStock) {
}
