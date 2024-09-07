package com.inventory.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemUpdateData;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Table(name = "items")
@Entity
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

    private Long userId;

    public Item() {
    }

    public Item(Long id, String itemName, String description, Categorie category, BigDecimal price,
                Integer numberInStock, Image image, Long userId) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.numberInStock = numberInStock;
        this.image = image;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Categorie getCategory() {
        return category;
    }

    public void setCategory(Categorie category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getNumberInStock() {
        return numberInStock;
    }

    public void setNumberInStock(Integer numberInStock) {
        this.numberInStock = numberInStock;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
