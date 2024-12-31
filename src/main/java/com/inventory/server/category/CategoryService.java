package com.inventory.server.category;

import com.inventory.server.category.dto.CategoryCreateMapper;
import com.inventory.server.category.dto.CategoryDTOMapper;
import com.inventory.server.category.dto.CategoryListData;
import com.inventory.server.category.dto.CreateCategoryData;
import com.inventory.server.infra.exception.ObjectAlreadyCreatedException;
import com.inventory.server.infra.exception.ObjectNotFoundException;
import com.inventory.server.user.User;
import com.inventory.server.user.UserRepository;
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

import static com.inventory.server.utils.UserGetter.getUserFromContext;

@Service
@Observed(name = "categoryService")
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDTOMapper categoryDTOMapper;
    private final CategoryCreateMapper categoryCreateMapper;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, CategoryDTOMapper categoryDTOMapper, CategoryCreateMapper categoryCreateMapper, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryDTOMapper = categoryDTOMapper;
        this.categoryCreateMapper = categoryCreateMapper;
        this.userRepository = userRepository;
    }

    public Page<CategoryListData> listAllCategories(Pageable pagination) {
        return categoryRepository.findAllActive(pagination).map(categoryDTOMapper);
    }

    public CategoryListData listCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Category"));

        return categoryDTOMapper.apply(category);
    }

    @Transactional
    public CreateRecordUtil createCategory(CreateCategoryData data, UriComponentsBuilder uriBuilder) {
        User user = getUserFromContext();

        boolean isNameInUse = categoryRepository
                .existsByUserIdAndCategoryNameIgnoreCaseAndIsDeletedFalse(user.getId(), data.categoryName());

        if (isNameInUse) {
            throw new ObjectAlreadyCreatedException(data.categoryName());
        }

        Category category = new Category(data);
        category.setUser(user);
        category.updateTime();

        categoryRepository.save(category);

        URI uri = uriBuilder.path("/categories/{id}").buildAndExpand(category.getId()).toUri();

        CreateCategoryData listData = categoryCreateMapper.apply(category);

        return new CreateRecordUtil(listData, uri);
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Category"));

        categoryRepository.delete(category);
    }

    @Transactional
    public CreateCategoryData updateCategory(Long id, CreateCategoryData data) {
        User user = getUserFromContext();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, "Category"));

        if (data.categoryName() != null) {
            boolean isNameInUse = categoryRepository
                    .existsByUserIdAndCategoryNameIgnoreCaseAndIsDeletedFalse(user.getId(), data.categoryName());
            boolean isNameInUseBySameRecord = !data.categoryName().equals(category.getCategoryName());

            if (isNameInUse && isNameInUseBySameRecord) {
                throw new ObjectAlreadyCreatedException(data.categoryName());
            }
        }

        category.updateData(data);
        categoryRepository.save(category);

        return categoryCreateMapper.apply(category);
    }

    public Page<CategoryListData> findByCriteria(Map<String, String> searchCriteria, Pageable pagination) {
        User user = getUserFromContext();

        Specification<Category> spec = Specification.where(null);

        spec = spec.and(CategorySpecs.hasUserId(user.getId()));

        if (StringUtils.hasLength(searchCriteria.get("categoryName"))) {
            spec = spec.and(CategorySpecs.containsCategoryName(searchCriteria.get("categoryName")));
        }

        if (StringUtils.hasLength(searchCriteria.get("description"))) {
            spec = spec.and(CategorySpecs.containsDescription(searchCriteria.get("description")));
        }

        Page<Category> categories = categoryRepository.findAll(spec, pagination);

        return categories.map(categoryDTOMapper);
    }

    public Page<CategoryListData> findActiveAndDeletedCategories(Pageable pagination, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("user not found");
        }

        return categoryRepository.findAll(pagination, userId).map(categoryDTOMapper);
    }
}
