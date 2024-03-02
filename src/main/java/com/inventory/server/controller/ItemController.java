package com.inventory.server.controller;

import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.CreateItemData;
import com.inventory.server.dto.ItemUpdateData;
import com.inventory.server.service.ItemService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository itemRepository;

    private final ItemService itemService;

    public ItemController(ItemRepository itemRepository, ItemService itemService) {
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity getItems(@PageableDefault(sort = "itemName") Pageable pagination) {
        return ResponseEntity.ok(itemRepository.findAll(pagination));
    }

    @GetMapping("/{id}")
    public ResponseEntity itemsByCategoryId(@PathVariable Long id, @PageableDefault(sort = "itemName") Pageable pagination) {

        return itemService.itemsByCategoryId(id, pagination);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity detailItemById(@PathVariable Long id) {
        return itemService.detailItemById(id);
    }

    @PostMapping
    @Transactional
    public ResponseEntity createItem(@RequestBody @Valid CreateItemData data, UriComponentsBuilder uriBuilder) {
        return itemService.createItem(data, uriBuilder);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deleteItemById(@PathVariable Long id) {
        return itemService.deleteItemById(id);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity updateItemById(@RequestBody @Valid ItemUpdateData data, @PathVariable Long id) {
        return itemService.updateItemById(data, id);
    }
}
