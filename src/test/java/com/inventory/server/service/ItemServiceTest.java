package com.inventory.server.service;

import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.infra.exception.ItemAlreadyCreatedException;
import com.inventory.server.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    private CreateItemData data;

    @Mock
    private UriComponentsBuilder uriBuilder;

    @Mock
    private UriComponents uriComponents;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Test
    void itemIsSavedToDatabaseWhenDataIsValid() throws ItemAlreadyCreatedException {
        // ARRANGE
        this.data = new CreateItemData("Card", "Mock card", 11L, new BigDecimal("11.00"), 42);
        given(uriBuilder.path(stringCaptor.capture())).willReturn(uriBuilder);
        given(uriBuilder.buildAndExpand(longCaptor.capture())).willReturn(uriComponents);

        // ACT
        itemService.createItem(data, uriBuilder);

        // ASSERT
        then(itemRepository).should().save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();
        Assertions.assertEquals(savedItem.getItemName(), "Card");
        Assertions.assertEquals(savedItem.getNumberInStock(), 42);
    }

    @Test
    void doesNotAllowToCreateWithNameAlreadyInUse() {
        // simula inserção de um record
        // simula criação de record com mesmo nome do anterior

    }
}