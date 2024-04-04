package com.inventory.server.controller;

import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemUpdateData;
import com.inventory.server.infra.exception.ItemAlreadyCreatedException;
import com.inventory.server.model.Item;
import com.inventory.server.service.ItemService;
import com.inventory.server.utils.CreateRecordUtil;
import jakarta.validation.Valid;
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
        return ResponseEntity.ok(itemService.findAllItems(pagination));
    }

    @GetMapping("/{id}")
    public ResponseEntity itemsByCategoryId(@PathVariable Long id, @PageableDefault(sort = "itemName") Pageable pagination) {
        return ResponseEntity.ok(itemService.itemsByCategoryId(id, pagination));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity detailItemById(@PathVariable Long id) {
        if (itemRepository.existsById(id)) {
            return ResponseEntity.ok(itemService.detailItemById(id));
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity createItem(@RequestBody @Valid CreateItemData data, UriComponentsBuilder uriBuilder) throws ItemAlreadyCreatedException {
        CreateRecordUtil record = itemService.createItem(data, uriBuilder);

        return ResponseEntity.created(record.getUri()).body(record.getObject());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteItemById(@PathVariable Long id) {
        if (itemRepository.existsById(id)) {
            itemService.deleteItemById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity updateItemById(@RequestBody @Valid ItemUpdateData data, @PathVariable Long id) throws ItemAlreadyCreatedException {
        if (itemRepository.existsById(id)) {
            Item item = itemService.updateItemById(data, id);
            return ResponseEntity.ok(item);
        }

        return ResponseEntity.notFound().build();
    }
}
