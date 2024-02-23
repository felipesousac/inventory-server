package com.inventory.server.service;

import com.inventory.server.domain.CategorieRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.CreateItemData;
import com.inventory.server.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategorieRepository categorieRepository;


    public ResponseEntity itemsByCategoryId(Long id) {
        return ResponseEntity.ok(itemRepository.findByCategoryId(id));
    }

    public ResponseEntity detailItemById(Long id) {
        Item item = itemRepository.getReferenceById(id);
        return ResponseEntity.ok(item);
    }

    public ResponseEntity createItem(CreateItemData data, UriComponentsBuilder uriBuilder) {
        Item item = new Item(data);
        itemRepository.save(item);

        URI uri = uriBuilder.path("/items/{id}/detail").buildAndExpand(item.getId()).toUri();

        return ResponseEntity.created(uri).body(item);
    }
}
