package com.inventory.server.controller;

import com.inventory.server.domain.ItemRepository;
import com.inventory.server.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity getItems(@PageableDefault(sort = "itemName") Pageable pagination) {
        return ResponseEntity.ok(itemRepository.findAll(pagination));
    }

    @GetMapping("/{id}")
    public ResponseEntity itemsByCategoryId(@PathVariable Long id) {
        return itemService.itemsByCategoryId(id);
    }

}
