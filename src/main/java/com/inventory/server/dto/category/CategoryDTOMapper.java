package com.inventory.server.dto.category;

import com.inventory.server.model.Categorie;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CategoryDTOMapper implements Function<Categorie, CategoryListData> {

    @Override
    public CategoryListData apply(Categorie category) {
        return new CategoryListData(
                category.getId(),
                category.getCategoryName());
    }
}
