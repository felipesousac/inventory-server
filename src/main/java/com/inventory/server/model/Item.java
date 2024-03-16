package com.inventory.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemUpdateData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "items")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String description;

    @Column(name = "categorie_id")
    private Long categoryId;

    private BigDecimal price;

    private Integer numberInStock;

    public Item(CreateItemData data) {
        this.itemName = data.itemName();
        this.description = data.description();
        this.categoryId = data.categoryId();
        this.price = data.price();
        this.numberInStock = data.numberInStock();
    }

    public void updateData(ItemUpdateData data) {
        this.itemName = data.itemName();
        this.description = data.description();
        this.price = data.price();
        this.numberInStock = data.numberInStock();
    }
}
