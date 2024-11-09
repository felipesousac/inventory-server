package com.inventory.server.service;

import com.inventory.server.domain.CategoryRepository;
import com.inventory.server.dto.category.CategoryCreateMapper;
import com.inventory.server.dto.category.CategoryDTOMapper;
import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.dto.category.CreateCategoryData;
import com.inventory.server.infra.exception.ObjectAlreadyCreatedException;
import com.inventory.server.infra.exception.ObjectNotFoundException;
import com.inventory.server.model.Category;
import com.inventory.server.model.User;
import com.inventory.server.utils.CreateRecordUtil;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Observed(name = "categoryService")
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
                .orElseThrow(() -> new ObjectNotFoundException(id));

        return categoryDTOMapper.apply(category);
    }

    @Transactional
    public CreateRecordUtil registerCategory(CreateCategoryData data, UriComponentsBuilder uriBuilder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        boolean isNameInUse = categoryRepository
                .existsByUserIdAndCategoryNameIgnoreCase(userId, data.categoryName());

        if (isNameInUse) {
            throw new ObjectAlreadyCreatedException(data.categoryName());
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
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));

        categoryRepository.delete(category);
    }

    @Transactional
    public CreateCategoryData updateCategory(Long id, CreateCategoryData data) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((User) authentication.getPrincipal()).getId();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id));

        boolean isNameInUse = categoryRepository
                .existsByUserIdAndCategoryNameIgnoreCase(userId, data.categoryName());

        boolean isNameInUseBySameRecord = !data.categoryName().equals(category.getCategoryName());

        if (isNameInUse && isNameInUseBySameRecord) {
            throw new ObjectAlreadyCreatedException(data.categoryName());
        }

        category.updateData(data);
        categoryRepository.save(category);

        return categoryCreateMapper.apply(category);
    }
}
