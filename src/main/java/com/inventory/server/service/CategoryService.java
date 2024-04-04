package com.inventory.server.service;

import com.inventory.server.domain.CategorieRepository;
import com.inventory.server.dto.category.CategoryDTOMapper;
import com.inventory.server.dto.category.CategoryListData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategorieRepository categorieRepository;
    private final CategoryDTOMapper categoryDTOMapper;

    public CategoryService(CategorieRepository categorieRepository, CategoryDTOMapper categoryDTOMapper) {
        this.categorieRepository = categorieRepository;
        this.categoryDTOMapper = categoryDTOMapper;
    }

    public Page<CategoryListData> listAllCategories(Pageable pagination) {
        return categorieRepository.findAll(pagination).map(categoryDTOMapper);
    }

}
