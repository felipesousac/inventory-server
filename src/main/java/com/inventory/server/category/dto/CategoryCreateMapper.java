package com.inventory.server.category.dto;

import com.inventory.server.category.Category;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CategoryCreateMapper implements Function<Category, CreateCategoryData> {

    @Override
    public CreateCategoryData apply(Category category) {
        return new CreateCategoryData(category.getCategoryName(), category.getDescription());
    }
}
