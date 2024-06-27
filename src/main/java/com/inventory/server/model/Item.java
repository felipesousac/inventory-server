package com.inventory.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemUpdateData;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "items")
@Entity
@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie category;

    private BigDecimal price;

    private Integer numberInStock;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    public Item(CreateItemData data) {
        this.itemName = data.itemName();
        this.description = data.description();
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
