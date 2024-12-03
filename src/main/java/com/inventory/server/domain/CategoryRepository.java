package com.inventory.server.domain;

import com.inventory.server.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query(value = "SELECT c FROM Category c WHERE c.userId = ?#{principal?.id}")
    Page<Category> findAll(Pageable pagination);

    @Query(value = "SELECT d FROM Category d WHERE d.userId = ?#{principal?.id} AND d.id = :id")
    Optional<Category> findById(Long id);

    boolean existsByUserIdAndCategoryNameIgnoreCase(Long userId, String categoryName);

    boolean existsByIdAndUserId(Long id, Long userId);
}
