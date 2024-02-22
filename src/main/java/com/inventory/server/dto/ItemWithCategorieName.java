package com.inventory.server.dto;

import com.inventory.server.model.Categorie;
import com.inventory.server.model.Item;

import java.math.BigDecimal;

public class ItemWithCategorieName {

    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer numberInStock;

    public ItemWithCategorieName(Item item, Categorie categorie) {
        this.id = item.getId();
        this.name = item.getItemName();
        this.description = item.getDescription();
        this.categoryId = item.getCategoryId();
        this.categoryName = categorie.getCategoryName();
        this.price = item.getPrice();
        this.numberInStock = item.getNumberInStock();
    }
}
