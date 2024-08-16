package com.inventory.server.dto.item;

import com.inventory.server.dto.category.CategoryDTOMapper;
import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.model.Item;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ItemDTOMapper implements Function<Item, ItemListData> {

    private final CategoryDTOMapper categoryDTOMapper;

    public ItemDTOMapper(CategoryDTOMapper categoryDTOMapper) {
        this.categoryDTOMapper = categoryDTOMapper;
    }

    @Override
    public ItemListData apply(Item item) {
        CategoryListData category = categoryDTOMapper.apply(item.getCategory());

        return new ItemListData(
                item.getId(),
                item.getItemName(),
                category,
                item.getDescription(),
                item.getPrice(),
                item.getNumberInStock()
                );
    }
}
