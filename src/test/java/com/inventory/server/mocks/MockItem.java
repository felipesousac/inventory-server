package com.inventory.server.mocks;

import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemListData;
import com.inventory.server.model.Categorie;
import com.inventory.server.model.Item;

import java.math.BigDecimal;

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

    public Item mockEntity(Integer number) {
        Item item = new Item();
        Categorie category = new Categorie(11L, "mockCategory", "mockDescription");

        item.setId(number.longValue());
        item.setItemName("Name Test" + number);
        item.setDescription("Name Description" + number);
        item.setCategory(category);
        item.setPrice(BigDecimal.valueOf(number));
        item.setNumberInStock(number);

        return item;
    }

    public CreateItemData mockDTO(Integer number) {
        CreateItemData data = new CreateItemData(
                "Name Test" + number,
                "Name Description" + number,
                11L,
                BigDecimal.valueOf(number),
                number);

        return data;
    }

    private ItemListData itemListData(Integer number) {
        CategoryListData category = new CategoryListData(11L, "mockCategory");

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
