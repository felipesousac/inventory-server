package com.inventory.server.service;

import com.inventory.server.domain.CategorieRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemDTOMapper;
import com.inventory.server.dto.item.ItemListData;
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

    private final ItemDTOMapper itemDTOMapper;

    public ItemService(ItemRepository itemRepository, CategorieRepository categorieRepository, ItemDTOMapper itemDTOMapper) {
        this.itemRepository = itemRepository;
        this.categorieRepository = categorieRepository;
        this.itemDTOMapper = itemDTOMapper;
    }

    public Page<ItemListData> findAllItems(Pageable pagination) {
        return itemRepository.findAll(pagination).map(itemDTOMapper);
    }


    public Page<ItemListData> itemsByCategoryId(Long id, Pageable pagination) {
        Categorie category = categorieRepository.getReferenceById(id);

        return itemRepository.findByCategory(category, pagination).map(itemDTOMapper);
    }

    public ItemListData detailItemById(Long id) {
        ItemListData item = itemDTOMapper.apply(itemRepository.getReferenceById(id));

        return item;
    }

    @Transactional
    public CreateRecordUtil createItem(CreateItemData data, UriComponentsBuilder uriBuilder) throws ItemAlreadyCreatedException {
        boolean isNameInUse = itemRepository.findByItemNameIgnoreCase(data.itemName()).isPresent();

        if (isNameInUse) {
            throw new ItemAlreadyCreatedException("There is a item created with this name");
        }

        Item item = new Item(data);
        Categorie category = categorieRepository.getReferenceById(data.categoryId());
        item.setCategory(category);
        itemRepository.save(item);

        URI uri = uriBuilder.path("/items/{id}/detail").buildAndExpand(item.getId()).toUri();

        ItemListData newItem = itemDTOMapper.apply(item);

        return new CreateRecordUtil(newItem, uri);
    }

    @Transactional
    public void deleteItemById(Long id) {
        Item item = itemRepository.getReferenceById(id);
        itemRepository.delete(item);
    }

    @Transactional
    public ItemListData updateItemById(ItemUpdateData data, Long id) throws ItemAlreadyCreatedException {
        Item item = itemRepository.getReferenceById(id);

        boolean isNameInUse = itemRepository.findByItemNameIgnoreCase(data.itemName()).isPresent();
        boolean isNameInUseBySameRecord = !data.itemName().equals(item.getItemName());

        if (isNameInUse && isNameInUseBySameRecord) {
            throw new ItemAlreadyCreatedException("There is an item created with this name");
        }

        item.updateData(data);

        return itemDTOMapper.apply(item);
    }
}
