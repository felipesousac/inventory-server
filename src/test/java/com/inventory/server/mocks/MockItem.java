package com.inventory.server.mocks;

import com.inventory.server.category.dto.CategoryListData;
import com.inventory.server.item.dto.CreateItemData;
import com.inventory.server.item.dto.ItemListData;
import com.inventory.server.category.Category;
import com.inventory.server.item.Item;
import com.inventory.server.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class MockItem {

    public Item mockEntity() {
        return mockEntity(0);
    }

    public CreateItemData mockDTO() {
        return mockDTO(0);
    }

    public ItemListData mockItemListData() {
        return itemListData(0);
    }

    public MockCategory mockCategory = new MockCategory();

    public Item mockEntity(Integer number) {
        Item item = new Item();

        OffsetDateTime time = OffsetDateTime.now();
        LocalDateTime localDateTime = time.toLocalDateTime();
        String offset = time.getOffset().getId();
        Category category = mockCategory.mockEntity();

        User user = new User();
        user.setId(1L);

        item.setItemName("Name Test" + number);
        item.setDescription("Name Description" + number);
        item.setCategory(category);
        item.setPrice(BigDecimal.valueOf(number));
        item.setNumberInStock(number);
        item.setCreatedAt(localDateTime);
        item.setOffset(offset);
        item.setUser(user); // admin userId
        item.setImgUrl("items_img");

        return item;
    }

    public CreateItemData mockDTO(Integer number) {
        CreateItemData data = new CreateItemData(
                "Name Test" + number,
                "Name Description" + number,
                number.longValue(),
                BigDecimal.valueOf(number),
                number);

        return data;
    }

    private ItemListData itemListData(Integer number) {
        CategoryListData category = new CategoryListData(number.longValue(), "mockCategory", "mockDescription");

        ItemListData data = new ItemListData(
                number.longValue(),
                "First Name Test" + number,
                category,
                "Name Description" + number,
                BigDecimal.valueOf(number),
                number
        );

        return data;
    }
}
