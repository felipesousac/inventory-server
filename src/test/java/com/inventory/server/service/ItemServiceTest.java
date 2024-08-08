package com.inventory.server.service;

import com.inventory.server.domain.CategorieRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemDTOMapper;
import com.inventory.server.infra.exception.ItemAlreadyCreatedException;
import com.inventory.server.mocks.MockItem;
import com.inventory.server.model.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    MockItem input;

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    CategorieRepository categorieRepository;

    @Mock
    ItemDTOMapper itemDTOMapper;

    @Mock
    private UriComponentsBuilder uriBuilder;

    @Mock
    private UriComponents uriComponents;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @BeforeEach
    void setUpMocks() {
        input = new MockItem();
    }

    @Test
    void itemIsNotSavedToDatabaseWhenThereIsARecordWithSameName() throws ItemAlreadyCreatedException {
        Item item = input.mockEntity();
        CreateItemData data = input.mockDTO();

        when(itemRepository.findByItemNameIgnoreCase(any())).thenReturn(Optional.of(item));

        Exception ex = assertThrows(ItemAlreadyCreatedException.class, () -> {
            itemService.createItem(data, uriBuilder);
        });

        String expectedMessage = "There is an item created with this name";
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void itemIsSavedToDatabaseWhenDataIsValid() throws ItemAlreadyCreatedException {
        // ARRANGE
        var data = new CreateItemData("Card", "Mock card", 11L, new BigDecimal("11.00"), 42);
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
}
