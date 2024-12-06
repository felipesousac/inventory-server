package com.inventory.server.item.dto;

import com.inventory.server.category.dto.CategoryDTOMapper;
import com.inventory.server.category.dto.CategoryListData;
import com.inventory.server.item.Item;
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
