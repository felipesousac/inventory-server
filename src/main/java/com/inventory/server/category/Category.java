package com.inventory.server.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.inventory.server.category.dto.CreateCategoryData;
import com.inventory.server.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

@Table(name = "categories")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLDelete(sql = "UPDATE categories SET is_deleted = true WHERE id = ?")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    private String offset;

    private boolean isDeleted = Boolean.FALSE;

    public Category() {
    }

    public Category(Long id, String categoryName, String description, User user, LocalDateTime createdAt,
     String offset) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
        this.user = user;
        this.createdAt = createdAt;
        this.offset = offset;
    }

    public Category(CreateCategoryData data) {
        this.categoryName = data.categoryName();
        this.description = data.description();
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void updateData(CreateCategoryData data) {
        if (data.categoryName() != null) {
            this.categoryName = data.categoryName();
        }

        if (data.description() != null) {
            this.description = data.description();
        }
    }

    public void updateTime() {
        OffsetDateTime time = OffsetDateTime.now();
        LocalDateTime localDateTime = time.toLocalDateTime();
        String offset = time.getOffset().getId();

        this.createdAt = localDateTime;
        this.offset = offset;
    }
}
