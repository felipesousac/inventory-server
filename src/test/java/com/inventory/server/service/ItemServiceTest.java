package com.inventory.server.service;

import com.inventory.server.domain.CategorieRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemDTOMapper;
import com.inventory.server.infra.exception.ItemAlreadyCreatedException;
import com.inventory.server.model.Item;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ItemServiceTest {

    @Mock
    private UriComponentsBuilder uriBuilder;

    @Mock
    private UriComponents uriComponents;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @Mock
    EntityManager em;

    @Mock
    private ItemDTOMapper itemDTOMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mustNotAllowCreateItemWithExistingName() throws ItemAlreadyCreatedException {
        CreateItemData createItemData = new CreateItemData(
                "Card",
                "Mock card",
                11L,
                new BigDecimal("11.00"),
                42);

        given(uriBuilder.path(stringCaptor.capture())).willReturn(uriBuilder);
        given(uriBuilder.buildAndExpand(longCaptor.capture())).willReturn(uriComponents);

        Item item = new Item(createItemData);

        //when(itemRepository.findByItemNameIgnoreCase(item.getItemName())).thenReturn(Optional.of(item));

        itemService.createItem(createItemData, uriBuilder);

        Optional<Item> card = itemRepository.findByItemNameIgnoreCase("Card");
        //System.out.println(card.get().getItemName());

        Exception ex = assertThrows(ItemAlreadyCreatedException.class, () -> {
            itemService.createItem(createItemData, uriBuilder);
        });

        String expectedMessage = "There is a item created with this name";
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }



    //    @InjectMocks
//    private ItemService itemService;
//
//    @Mock
//    private ItemRepository itemRepository;
//
//    private CreateItemData data;
//
//    @Mock
//    private UriComponentsBuilder uriBuilder;
//
//    @Mock
//    private UriComponents uriComponents;
//
//    @Captor
//    private ArgumentCaptor<Item> itemCaptor;
//
//    @Captor
//    private ArgumentCaptor<Long> longCaptor;
//
//    @Captor
//    private ArgumentCaptor<String> stringCaptor;
//
//    @Test
//    void itemIsSavedToDatabaseWhenDataIsValid() throws ItemAlreadyCreatedException {
//        // ARRANGE
//        this.data = new CreateItemData("Card", "Mock card", 11L, new BigDecimal("11.00"), 42);
//        given(uriBuilder.path(stringCaptor.capture())).willReturn(uriBuilder);
//        given(uriBuilder.buildAndExpand(longCaptor.capture())).willReturn(uriComponents);
//
//        // ACT
//        itemService.createItem(data, uriBuilder);
//
//        // ASSERT
//        then(itemRepository).should().save(itemCaptor.capture());
//        Item savedItem = itemCaptor.getValue();
//        Assertions.assertEquals(savedItem.getItemName(), "Card");
//        Assertions.assertEquals(savedItem.getNumberInStock(), 42);
//    }
//
//    @Test
//    void doesNotAllowToCreateWithNameAlreadyInUse() {
//        // simula inserção de um record
//        // simula criação de record com mesmo nome do anterior
//
//    }
}