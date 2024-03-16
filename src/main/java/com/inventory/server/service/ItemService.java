package com.inventory.server.service;

import com.inventory.server.domain.CategorieRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemUpdateData;
import com.inventory.server.model.Item;
import com.inventory.server.utils.CreateRecordUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    public Page<Item> itemsByCategoryId(Long id, Pageable pagination) {
        return itemRepository.findByCategoryId(id, pagination);
    }

    public Item detailItemById(Long id) {
        return itemRepository.getReferenceById(id);
    }

    public CreateRecordUtil createItem(CreateItemData data, UriComponentsBuilder uriBuilder) {
        Item item = new Item(data);
        itemRepository.save(item);

        URI uri = uriBuilder.path("/items/{id}/detail").buildAndExpand(item.getId()).toUri();

        return new CreateRecordUtil(item, uri);
    }

    public void deleteItemById(Long id) {
        Item item = itemRepository.getReferenceById(id);
        itemRepository.delete(item);
    }

    public Item updateItemById(ItemUpdateData data, Long id) {
        Item item = itemRepository.getReferenceById(id);
        item.updateData(data);

        return item;
    }
}
