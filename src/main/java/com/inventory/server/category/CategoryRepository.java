package com.inventory.server.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>,
        JpaSpecificationExecutor<Category> {

    @Query(value = "SELECT c FROM Category c WHERE c.user.id = ?#{principal?.id} AND c.isDeleted = false")
    Page<Category> findAllActive(Pageable pagination);

    @Query(value = "SELECT c FROM Category c WHERE c.user.id = :userId")
    Page<Category> findAll(Pageable pagination, Long userId);

    @Query(value = "SELECT d FROM Category d WHERE d.user.id = ?#{principal?.id} AND d.id = :id AND d" +
            ".isDeleted" +
            " = false")
    Optional<Category> findById(Long id);

    boolean existsByUserIdAndCategoryNameIgnoreCaseAndIsDeletedFalse(Long userId, String categoryName);
}
