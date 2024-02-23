package com.inventory.server.model;

import com.inventory.server.dto.CreateItemData;
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
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    private String description;

    @Column(name = "categorie_id")
    private Long categoryId;

    private BigDecimal price;

    @Column(name = "number_in_stock")
    private Integer numberInStock;

    public Item(CreateItemData data) {
        this.itemName = data.itemName();
        this.description = data.description();
        this.categoryId = data.categoryId();
        this.price = data.price();
        this.numberInStock = data.numberInStock();
    }

}