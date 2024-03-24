package com.inventory.server.domain;

import com.inventory.server.model.Categorie;
import com.inventory.server.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByCategory(Categorie category, Pageable pagination);
    Optional<Item> findByItemName(String name);
}
