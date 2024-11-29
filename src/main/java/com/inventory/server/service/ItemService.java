package com.inventory.server.service;

import com.inventory.server.domain.CategoryRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemDTOMapper;
import com.inventory.server.dto.item.ItemListData;
import com.inventory.server.dto.item.ItemUpdateData;
import com.inventory.server.infra.exception.*;
import com.inventory.server.model.Category;
import com.inventory.server.model.Image;
import com.inventory.server.model.Item;
import com.inventory.server.model.User;
import com.inventory.server.specification.ItemSpecs;
import com.inventory.server.utils.CreateRecordUtil;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
@Observed(name = "itemService")
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
        return itemRepository.findAll(pagination).map(itemDTOMapper);
    }

    public Page<ItemListData> itemsByCategoryId(Long id, Pageable pagination) {
        Category category = categoryRepository.getReferenceById(id);

        return itemRepository.findByCategory(category, pagination).map(itemDTOMapper);
    }

    @Transactional(readOnly = true)
    public ItemListData detailItemById(Long id) {
        Item record =
                itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id));

        return itemDTOMapper.apply(record);
    }

    @Transactional
    public CreateRecordUtil createItem(CreateItemData data, UriComponentsBuilder uriBuilder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        boolean isNameInUse = itemRepository.existsByUserIdAndItemNameIgnoreCase(userId, data.itemName());

        if (isNameInUse) {
            throw new ObjectAlreadyCreatedException(data.itemName());
        }

        Item item = new Item(data);
        Category category = categoryRepository.getReferenceById(data.categoryId());
        item.setCategory(category);
        item.setUserId(((User) authentication.getPrincipal()).getId());
        itemRepository.save(item);

        URI uri = uriBuilder.path("/items/{id}/detail").buildAndExpand(item.getId()).toUri();

        ItemListData newItem = itemDTOMapper.apply(item);

        return new CreateRecordUtil(newItem, uri);
    }

    @Transactional
    public void deleteItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));
        itemRepository.delete(item);
    }

    @Transactional
    public ItemListData updateItemById(ItemUpdateData data, Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));

        boolean isNameInUse = itemRepository.existsByUserIdAndItemNameIgnoreCase(userId, data.itemName());
        boolean isNameInUseBySameRecord = !data.itemName().equals(item.getItemName());

        if (isNameInUse && isNameInUseBySameRecord) {
            throw new ObjectAlreadyCreatedException(data.itemName());
        }

        item.updateData(data);
        itemRepository.save(item);

        return itemDTOMapper.apply(item);
    }

    @Transactional
    public void uploadImageInItem(MultipartFile imageFile, Long itemId) throws IOException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(itemId));

        Image image = imageService.uploadImage(imageFile);

        if (item.getImage() != null) {
            imageService.deleteImage(item.getImage().getId());
        }

        item.setImage(image);
    }

    public Page<ItemListData> findByCriteria(Map<String, String> searchCriteria, Pageable pagination) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        Specification<Item> spec = Specification.where(null);

        spec = spec.and(ItemSpecs.hasId(userId)); // User can only find own items

        if (StringUtils.hasLength(searchCriteria.get("itemName"))) {
           spec = spec.and(ItemSpecs.containsItemName(searchCriteria.get("itemName")));
        }

        if (StringUtils.hasLength(searchCriteria.get("description"))) {
            spec = spec.and(ItemSpecs.containsDescription(searchCriteria.get("description")));
        }

        Page<Item> items = itemRepository.findAll(spec, pagination);

        return items.map(itemDTOMapper);
    }
}
