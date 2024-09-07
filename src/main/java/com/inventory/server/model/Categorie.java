package com.inventory.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inventory.server.dto.category.CreateCategoryData;
import jakarta.persistence.*;

import java.util.Objects;

@Table(name = "categories")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    private String description;

    private Long userId;

    public Categorie() {
    }

    public Categorie(Long id, String categoryName, String description, Long userId) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
        this.userId = userId;
    }

    public Categorie(CreateCategoryData data) {
        this.categoryName = data.categoryName();
        this.description = data.description();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie categorie = (Categorie) o;
        return Objects.equals(id, categorie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void updateData(CreateCategoryData data) {
        this.categoryName = data.categoryName();
        this.description = data.description();
    }
}
