package com.inventory.server.item;

import com.inventory.server.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    @Query(value = "SELECT d FROM Item d WHERE d.user.id = ?#{principal?.id} AND d.category = :category AND" +
            " d.category.isDeleted = false")
    Page<Item> findByCategory(Category category, Pageable pagination);

    @Query(value = "SELECT d FROM Item d WHERE d.user.id = ?#{principal?.id} AND d.id = :id AND d.isDeleted" +
            " = false")
    Optional<Item> findById(Long id);

    @Query(value = "SELECT d FROM Item d WHERE d.user.id = ?#{principal?.id} AND d.isDeleted = false")
    Page<Item> findAllActive(Pageable pagination);

    @Query(value = "SELECT d FROM Item d WHERE d.user.id = :userId")
    Page<Item> findAll(Pageable pagination, Long userId);

    boolean existsByUserIdAndItemNameIgnoreCaseAndIsDeletedFalse(Long id, String name);
}
