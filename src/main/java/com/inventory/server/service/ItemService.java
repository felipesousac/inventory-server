package com.inventory.server.service;

import com.inventory.server.domain.CategorieRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemUpdateData;
import com.inventory.server.infra.exception.ItemAlreadyCreatedException;
import com.inventory.server.model.Categorie;
import com.inventory.server.model.Item;
import com.inventory.server.utils.CreateRecordUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategorieRepository categorieRepository;

    public ItemService(ItemRepository itemRepository, CategorieRepository categorieRepository) {
        this.itemRepository = itemRepository;
        this.categorieRepository = categorieRepository;
    }


    public Page<Item> itemsByCategoryId(Long id, Pageable pagination) {
        Categorie category = categorieRepository.getReferenceById(id);


        return itemRepository.findByCategory(category, pagination);
    }

    public Item detailItemById(Long id) {
        return itemRepository.getReferenceById(id);
    }

    @Transactional
    public CreateRecordUtil createItem(CreateItemData data, UriComponentsBuilder uriBuilder) throws ItemAlreadyCreatedException {
        boolean isNameInUse = itemRepository.findByItemName(data.itemName()).isPresent();

        if (isNameInUse) {
            throw new ItemAlreadyCreatedException("There is a item created with this name");
        }

        Item item = new Item(data);
        Categorie category = categorieRepository.getReferenceById(data.categoryId());
        item.setCategory(category);
        itemRepository.save(item);

        URI uri = uriBuilder.path("/items/{id}/detail").buildAndExpand(item.getId()).toUri();

        return new CreateRecordUtil(item, uri);
    }

    @Transactional
    public void deleteItemById(Long id) {
        Item item = itemRepository.getReferenceById(id);
        itemRepository.delete(item);
    }

    @Transactional
    public Item updateItemById(ItemUpdateData data, Long id) throws ItemAlreadyCreatedException {
        boolean isNameInUse = itemRepository.findByItemName(data.itemName()).isPresent();

        if (isNameInUse) {
            throw new ItemAlreadyCreatedException("There is a item created with this name");
        }

        Item item = itemRepository.getReferenceById(id);
        item.updateData(data);

        return item;
    }
}
