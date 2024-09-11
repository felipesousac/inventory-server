package com.inventory.server.dto.category;

import com.inventory.server.model.Category;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CategoryCreateMapper implements Function<Category, CreateCategoryData> {

    @Override
    public CreateCategoryData apply(Category category) {
        return new CreateCategoryData(category.getCategoryName(), category.getDescription());
    }
}
