package com.inventory.server.category;

import com.inventory.server.category.dto.CategoryCreateMapper;
import com.inventory.server.category.dto.CategoryDTOMapper;
import com.inventory.server.category.dto.CategoryListData;
import com.inventory.server.category.dto.CreateCategoryData;
import com.inventory.server.infra.exception.ObjectAlreadyCreatedException;
import com.inventory.server.infra.exception.ObjectNotFoundException;
import com.inventory.server.utils.CreateRecordUtil;
import io.micrometer.observation.annotation.Observed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static com.inventory.server.utils.UserIdGetter.getUserIdFromContext;

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
        Long userId = getUserIdFromContext();

        boolean isNameInUse = categoryRepository
                .existsByUserIdAndCategoryNameIgnoreCase(userId, data.categoryName());

        if (isNameInUse) {
            throw new ObjectAlreadyCreatedException(data.categoryName());
        }

        Category category = new Category(data);
        category.setUserId(userId);
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
        Long userId = getUserIdFromContext();

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

    public Page<CategoryListData> findByCriteria(Map<String, String> searchCriteria, Pageable pagination) {
        Long userId = getUserIdFromContext();

        Specification<Category> spec = Specification.where(null);

        spec = spec.and(CategorySpecs.hasUserId(userId));

        if (StringUtils.hasLength(searchCriteria.get("categoryName"))) {
            spec = spec.and(CategorySpecs.containsCategoryName(searchCriteria.get("categoryName")));
        }

        if (StringUtils.hasLength(searchCriteria.get("description"))) {
            spec = spec.and(CategorySpecs.containsDescription(searchCriteria.get("description")));
        }

        Page<Category> categories = categoryRepository.findAll(spec, pagination);

        return categories.map(categoryDTOMapper);
    }
}
