package com.inventory.server.dto.category;

import com.inventory.server.model.Categorie;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CategoryCreateMapper implements Function<Categorie, CreateCategoryData> {

    @Override
    public CreateCategoryData apply(Categorie categorie) {
        return new CreateCategoryData(categorie.getCategoryName(), categorie.getDescription());
    }
}
