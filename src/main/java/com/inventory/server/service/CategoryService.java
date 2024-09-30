package com.inventory.server.service;

import com.inventory.server.domain.CategoryRepository;
import com.inventory.server.dto.category.CategoryCreateMapper;
import com.inventory.server.dto.category.CategoryDTOMapper;
import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.dto.category.CreateCategoryData;
import com.inventory.server.infra.exception.CategoryAlreadyCreatedException;
import com.inventory.server.infra.exception.CategoryNotFoundException;
import com.inventory.server.model.Category;
import com.inventory.server.model.User;
import com.inventory.server.utils.CreateRecordUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDTOMapper categoryDTOMapper;
    private final CategoryCreateMapper categoryCreateMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryDTOMapper categoryDTOMapper, CategoryCreateMapper categoryCreateMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryDTOMapper = categoryDTOMapper;
        this.categoryCreateMapper = categoryCreateMapper;
    }

    public Page<CategoryListData> listAllCategories(Pageable pagination) {
        return categoryRepository.findAll(pagination).map(categoryDTOMapper);
    }

    public CategoryListData listCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        return categoryDTOMapper.apply(category);
    }

    @Transactional
    public CreateRecordUtil registerCategory(CreateCategoryData data, UriComponentsBuilder uriBuilder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isNameInUse = categoryRepository.existsByUserIdAndCategoryNameIgnoreCase(
                ((User) authentication.getPrincipal()).getId(), data.categoryName()
        );

        if (isNameInUse) {
            throw new CategoryAlreadyCreatedException("There is a category created with this name");
        }

        Category category = new Category(data);
        category.setUserId(((User) authentication.getPrincipal()).getId());
        category.updateTime();

        categoryRepository.save(category);

        URI uri = uriBuilder.path("/categories/{id}/detail").buildAndExpand(category.getId()).toUri();

        CreateCategoryData listData = categoryCreateMapper.apply(category);

        return new CreateRecordUtil(listData, uri);
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        if (!existsByIdAndUserId(id, userId)) {
            throw new CategoryNotFoundException("Category not found");
        }

        Category category = categoryRepository.getReferenceById(id);
        categoryRepository.delete(category);
    }

    @Transactional
    public CreateCategoryData updateCategory(Long id, CreateCategoryData data) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        if (!existsByIdAndUserId(id, userId)) {
            throw new CategoryNotFoundException("Category not found");
        }

        Category category = categoryRepository.getReferenceById(id);

        boolean isNameInUse = categoryRepository.existsByUserIdAndCategoryNameIgnoreCase(
                ((User) authentication.getPrincipal()).getId(), data.categoryName()
        );
        boolean isNameInUseBySameRecord = !data.categoryName().equals(category.getCategoryName());

        if (isNameInUse && isNameInUseBySameRecord) {
            throw new CategoryAlreadyCreatedException("There is a category created with this name");
        }

        category.updateData(data);
        categoryRepository.save(category);

        return categoryCreateMapper.apply(category);
    }

    public boolean existsByIdAndUserId(Long id, Long userId) {
        return categoryRepository.existsByIdAndUserId(id, userId);
    }
}
