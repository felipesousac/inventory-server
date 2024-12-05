package com.inventory.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemUpdateData;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
    private Category category;

    private BigDecimal price;

    private Integer numberInStock;

    private Long userId;

    private String imgUrl;

    private LocalDateTime createdAt;

    private String offset;

    public Item() {
    }

    public Item(Long id, String itemName, String description, Category category, BigDecimal price, Integer numberInStock, Long userId, String imgUrl, LocalDateTime createdAt, String offset) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.numberInStock = numberInStock;
        this.userId = userId;
        this.imgUrl = imgUrl;
        this.createdAt = createdAt;
        this.offset = offset;
    }

    public Item(CreateItemData data) {
        this.itemName = data.itemName();
        this.description = data.description();
        this.price = data.price();
        this.numberInStock = data.numberInStock();
        updateTime();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void updateData(ItemUpdateData data) {
        this.itemName = data.itemName();
        this.description = data.description();
        this.price = data.price();
        this.numberInStock = data.numberInStock();
    }

    public void updateTime() {
        OffsetDateTime time = OffsetDateTime.now();
        LocalDateTime localDateTime = time.toLocalDateTime();
        String offset = time.getOffset().getId();

        this.createdAt = localDateTime;
        this.offset = offset;
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
