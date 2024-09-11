package com.inventory.server.domain;

import com.inventory.server.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT c FROM Category c WHERE c.userId = ?#{principal?.id}")
    Page<Category> findAll(Pageable pagination);

    boolean existsByUserIdAndCategoryNameIgnoreCase(Long userId, String categoryName);

    boolean existsByIdAndUserId(Long id, Long userId);
}
