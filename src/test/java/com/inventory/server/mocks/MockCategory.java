package com.inventory.server.mocks;

import com.inventory.server.category.dto.CategoryListData;
import com.inventory.server.category.dto.CreateCategoryData;
import com.inventory.server.category.Category;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class MockCategory {

    public Category mockEntity() {
        return mockEntity(0);
    }

    public CreateCategoryData mockCreateCategoryData() {
        return mockCreateCategoryData(0);
    }

    public CategoryListData mockCategoryListData() {
        return categoryListData(0);
    }

    private Category mockEntity(Integer number) {
        Category category = new Category();

        OffsetDateTime time = OffsetDateTime.now();
        LocalDateTime localDateTime = time.toLocalDateTime();
        String offset = time.getOffset().getId();

        category.setId(number.longValue());
        category.setCategoryName("Name Test" + number);
        category.setDescription("Name Description" + number);
        category.setCreatedAt(localDateTime);
        category.setOffset(offset);

        return category;
    }

    private CreateCategoryData mockCreateCategoryData(Integer number) {

        return new CreateCategoryData(
                "Name Test" + number,
                "Name Description" + number);
    }

    private CategoryListData categoryListData(Integer number) {

        return new CategoryListData(
                number.longValue(),
                "mockCategory:" + number,
                "mockDescription: " + number);
    }
}
