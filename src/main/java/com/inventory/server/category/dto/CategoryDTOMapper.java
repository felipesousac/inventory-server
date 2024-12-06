package com.inventory.server.category.dto;

import com.inventory.server.category.Category;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CategoryDTOMapper implements Function<Category, CategoryListData> {

    @Override
    public CategoryListData apply(Category category) {
        return new CategoryListData(
                category.getId(),
                category.getCategoryName(),
                category.getDescription());
    }
}
