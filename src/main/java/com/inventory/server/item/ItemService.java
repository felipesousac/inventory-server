package com.inventory.server.item;

import com.inventory.server.client.imagestorage.CloudinaryClient;
import com.inventory.server.category.CategoryRepository;
import com.inventory.server.item.dto.CreateItemData;
import com.inventory.server.item.dto.ItemDTOMapper;
import com.inventory.server.item.dto.ItemListData;
import com.inventory.server.item.dto.ItemUpdateData;
import com.inventory.server.infra.exception.*;
import com.inventory.server.category.Category;
import com.inventory.server.user.User;
import com.inventory.server.user.UserRepository;
import com.inventory.server.utils.CreateRecordUtil;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.inventory.server.utils.UserGetter.getUserFromContext;

@Service
@Observed(name = "itemService")
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ItemDTOMapper itemDTOMapper;
    private final CloudinaryClient cloudinaryClient;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository, ItemDTOMapper itemDTOMapper, CloudinaryClient cloudinaryClient, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.itemDTOMapper = itemDTOMapper;
        this.cloudinaryClient = cloudinaryClient;
        this.userRepository = userRepository;
    }

    public Page<ItemListData> listAllItems(Pageable pagination) {
        return itemRepository.findAllActive(pagination).map(itemDTOMapper);
    }

    public Page<ItemListData> listItemsByCategoryId(Long id, Pageable pagination) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Category"));

        return itemRepository.findByCategory(category, pagination).map(itemDTOMapper);
    }

    @Transactional(readOnly = true)
    public ItemListData listItemById(Long id) {
        Item record =
                itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "Item"));

        return itemDTOMapper.apply(record);
    }

    @Transactional
    public CreateRecordUtil createItem(CreateItemData data, UriComponentsBuilder uriBuilder) {
        User user = getUserFromContext();

        boolean isNameInUse = itemRepository.existsByUserIdAndItemNameIgnoreCaseAndIsDeletedFalse(user.getId(), data.itemName());

        if (isNameInUse) {
            throw new ObjectAlreadyCreatedException(data.itemName());
        }

        Item item = new Item(data);
        Category category = categoryRepository.findById(data.categoryId())
                .orElseThrow(() -> new ObjectNotFoundException(data.categoryId(), "Category"));

        item.setCategory(category);
        item.setUser(user);
        itemRepository.save(item);

        URI uri = uriBuilder.path("/items/{id}").buildAndExpand(item.getId()).toUri();

        ItemListData newItem = itemDTOMapper.apply(item);

        return new CreateRecordUtil(newItem, uri);
    }

    @Transactional
    public void deleteItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Item"));

        itemRepository.delete(item);
    }

    @Transactional
    public ItemListData updateItemById(ItemUpdateData data, Long id) {
        User user = getUserFromContext();

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Item"));

        if (data.itemName() != null) {
            boolean isNameInUse = itemRepository.existsByUserIdAndItemNameIgnoreCaseAndIsDeletedFalse(user.getId(), data.itemName());
            boolean isNameInUseBySameRecord = !data.itemName().equals(item.getItemName());

            if (isNameInUse && isNameInUseBySameRecord) {
                throw new ObjectAlreadyCreatedException(data.itemName());
            }
        }

        item.updateData(data);
        itemRepository.save(item);

        return itemDTOMapper.apply(item);
    }

    public Page<ItemListData> findByCriteria(Map<String, String> searchCriteria, Pageable pagination) {
        User user = getUserFromContext();

        Specification<Item> spec = Specification.where(null);

        spec = spec.and(ItemSpecs.hasUserId(user.getId())); // User can only find own records

        if (StringUtils.hasLength(searchCriteria.get("itemName"))) {
           spec = spec.and(ItemSpecs.containsItemName(searchCriteria.get("itemName")));
        }

        if (StringUtils.hasLength(searchCriteria.get("description"))) {
            spec = spec.and(ItemSpecs.containsDescription(searchCriteria.get("description")));
        }

        Page<Item> items = itemRepository.findAll(spec, pagination);

        return items.map(itemDTOMapper);
    }

    @Transactional
    public void uploadImage(Long itemId, MultipartFile image) throws IOException {
        if (!Objects.requireNonNull(image.getContentType()).contains("image")) {
            throw new FileNotSupportedException("Invalid file type - " + image.getContentType());
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(itemId, "Item"));

        if (!item.getImgUrl().equals("No available image")) {
            int beginIndex = item.getImgUrl().indexOf("items_img");
            int endIndex = item.getImgUrl().length() - 4; // minus image file format char size
            String PUBLIC_ID = (String) item.getImgUrl().subSequence(beginIndex, endIndex);

            cloudinaryClient.deleteImage(PUBLIC_ID);
        }

        String imgUrl = cloudinaryClient.uploadImage(itemId, image);

        item.setImgUrl(imgUrl);

        itemRepository.save(item);
    }

    public Page<ItemListData> findActiveAndDeletedItems(Pageable pagination, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("user not found");
        }

        return itemRepository.findAll(pagination, userId).map(itemDTOMapper);
    }

//    @Transactional
//    public void deleteImage(Long itemId) {
//        // TO-DO
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new ObjectNotFoundException(itemId, "Item"));
//
//        String imagePublicId = item.getImgUrl().replace("to-do");
//
//        item.setImgUrl("No available image");
//        itemRepository.save(item);
//
//        cloudinaryClient.deleteImage();
//    }
}
