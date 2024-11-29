package com.inventory.server.specification;

import com.inventory.server.model.Item;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecs {

    public static Specification<Item> hasId(Long providedId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), providedId);
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
