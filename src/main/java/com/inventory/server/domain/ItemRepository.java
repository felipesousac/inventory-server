package com.inventory.server.domain;

import com.inventory.server.model.Category;
import com.inventory.server.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    @Query(value = "SELECT d FROM Item d WHERE d.userId = ?#{principal?.id} AND d.category = :category")
    Page<Item> findByCategory(Category category, Pageable pagination);

    @Query(value = "SELECT d FROM Item d WHERE d.userId = ?#{principal?.id} AND d.id = :id")
    Optional<Item> findById(Long id);

    Optional<Item> findByItemNameIgnoreCase(String name);

    boolean existsByItemNameIgnoreCase(String name);

    Optional<Item> findByItemName(String itemName);

    @Query(value = "SELECT d FROM Item d WHERE d.userId = ?#{principal?.id}")
    Page<Item> findAll(Pageable pagination);

    boolean existsByUserIdAndItemNameIgnoreCase(Long id, String name);

    boolean existsByIdAndUserId(Long id, Long userId);
}
