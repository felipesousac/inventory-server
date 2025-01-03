package com.inventory.server.item;

import org.springframework.data.jpa.domain.Specification;

public class ItemSpecs {

    public static Specification<Item> hasUserId(Long providedUserId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), providedUserId);
    }

    public static Specification<Item> containsItemName(String providedItemName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("itemName")),
                "%" + providedItemName.toLowerCase() + "%");
    }

    public static Specification<Item> containsDescription(String providedDescription) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + providedDescription.toLowerCase() + "%");
    }
}
