package com.inventory.server.domain;

import com.inventory.server.model.Categorie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Categorie, Long> {

    @Query(value = "SELECT c FROM Categorie c WHERE c.userId = ?#{principal?.id}")
    Page<Categorie> findAll(Pageable pagination);

    boolean existsByUserIdAndCategoryNameIgnoreCase(Long userId, String categoryName);
}
