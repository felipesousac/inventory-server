package com.inventory.server.specification;

import com.inventory.server.model.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecs {

    public static Specification<Category> hasUserId(Long providedUserId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), providedUserId);
    }

    public static Specification<Category> containsCategoryName(String providedCategoryName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("categoryName"),
                "%" + providedCategoryName.toLowerCase() + "%");
    }

    public static Specification<Category> containsDescription(String providedDescription) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + providedDescription.toLowerCase() + "%");
    }
}
