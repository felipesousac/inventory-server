package com.inventory.server.service;

import com.inventory.server.domain.CategoryRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemDTOMapper;
import com.inventory.server.dto.item.ItemListData;
import com.inventory.server.dto.item.ItemUpdateData;
import com.inventory.server.infra.exception.FileNotSupportedException;
import com.inventory.server.infra.exception.ItemAlreadyCreatedException;
import com.inventory.server.model.Category;
import com.inventory.server.model.Image;
import com.inventory.server.model.Item;
import com.inventory.server.model.User;
import com.inventory.server.utils.CreateRecordUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ItemDTOMapper itemDTOMapper;
    private final ImageService imageService;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository, ItemDTOMapper itemDTOMapper, ImageService imageService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.itemDTOMapper = itemDTOMapper;
        this.imageService = imageService;
    }

    public Page<ItemListData> findAllItems(Pageable pagination) {
        OffsetDateTime time = OffsetDateTime.now();
        String offset = time.getOffset().getId();

        OffsetDateTime time1 = time.withOffsetSameInstant(ZoneOffset.of(String.valueOf(offset)));
        OffsetDateTime time2 = time.withOffsetSameInstant(ZoneOffset.of("-06:00"));

        return itemRepository.findAll(pagination).map(itemDTOMapper);
    }


    public Page<ItemListData> itemsByCategoryId(Long id, Pageable pagination) {
        Category category = categoryRepository.getReferenceById(id);

        return itemRepository.findByCategory(category, pagination).map(itemDTOMapper);
    }

    @Transactional(readOnly = true)
    public ItemListData detailItemById(Long id) {
        Optional<Item> record = itemRepository.findById(id);

        return itemDTOMapper.apply(record.get());
    }

    @Transactional
    public CreateRecordUtil createItem(CreateItemData data, UriComponentsBuilder uriBuilder) throws ItemAlreadyCreatedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isNameInUse = itemRepository.existsByUserIdAndItemNameIgnoreCase(
                ((User) authentication.getPrincipal()).getId(), data.itemName());

        if (isNameInUse) {
            throw new ItemAlreadyCreatedException("There is an item created with this name");
        }

        Item item = new Item(data);
        Category category = categoryRepository.getReferenceById(data.categoryId());
        item.setCategory(category);
        item.setUserId(((User) authentication.getPrincipal()).getId());
        item.updateTime();
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isNameInUse = itemRepository.existsByUserIdAndItemNameIgnoreCase(
                ((User) authentication.getPrincipal()).getId(), data.itemName());
        boolean isNameInUseBySameRecord = !data.itemName().equals(item.getItemName());

        if (isNameInUse && isNameInUseBySameRecord) {
            throw new ItemAlreadyCreatedException("There is an item created with this name");
        }

        item.updateData(data);

        return itemDTOMapper.apply(item);
    }

    @Transactional
    public void uploadImageInItem(MultipartFile imageFile, Long itemId) throws IOException, FileNotSupportedException {
        Image image = imageService.uploadImage(imageFile);
        Item item = itemRepository.getReferenceById(itemId);

        if (!(item.getImage() == null)) {
            imageService.deleteImage(item.getImage().getId());
        }

        item.setImage(image);
    }

    public boolean existsByIdAndUserId(Long id, Long userId) {
        return itemRepository.existsByIdAndUserId(id, userId);
    }
}
