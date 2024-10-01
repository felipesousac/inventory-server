package com.inventory.server.service;

import com.inventory.server.domain.CategoryRepository;
import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemDTOMapper;
import com.inventory.server.dto.item.ItemListData;
import com.inventory.server.dto.item.ItemUpdateData;
import com.inventory.server.infra.exception.ObjectAlreadyCreatedException;
import com.inventory.server.infra.exception.ObjectNotFoundException;
import com.inventory.server.mocks.MockItem;
import com.inventory.server.model.Item;
import com.inventory.server.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    CategoryRepository categoryRepository;

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
    void testDetailItemByIdSuccess() {
        // Given
        Item item = input.mockEntity();
        ItemListData data = input.mockItemListData();

        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        given(itemDTOMapper.apply(item)).willReturn(data);

        // When
        ItemListData returnedData = itemService.detailItemById(item.getId());

        // Then
        assertEquals(returnedData, data);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemDTOMapper, times(1)).apply(item);
    }

    @Test
    void testDetailItemByIdNotFound() {
        // Given
        given(itemRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        Exception ex = assertThrows(ObjectNotFoundException.class, () -> {
            itemService.detailItemById(anyLong());
        });

        // Then
        assertThat(ex).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void itemIsNotSavedToDatabaseWhenThereIsARecordWithSameName() {
        // Given
        CreateItemData data = input.mockDTO();

        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        given(itemRepository.existsByUserIdAndItemNameIgnoreCase(any(), any())).willReturn(true);

        // When
        Exception ex = assertThrows(ObjectAlreadyCreatedException.class, () -> {
            itemService.createItem(data, uriBuilder);
        });

        // Then
        assertThat(ex).isInstanceOf(ObjectAlreadyCreatedException.class);
    }

    @Test
    void itemIsSavedToDatabaseWhenDataIsValid() {
        // Given
        var data = new CreateItemData("Card", "Mock card", 11L, new BigDecimal("11.00"), 42);
        given(uriBuilder.path(stringCaptor.capture())).willReturn(uriBuilder);
        given(uriBuilder.buildAndExpand(longCaptor.capture())).willReturn(uriComponents);

        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        // When
        itemService.createItem(data, uriBuilder);

        // Then
        then(itemRepository).should().save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();
        Assertions.assertEquals(savedItem.getItemName(), "Card");
        Assertions.assertEquals(savedItem.getNumberInStock(), 42);
    }

    @Test
    void deleteItemSuccess() {
        // Given
        Item item = input.mockEntity();
        given(itemRepository.getReferenceById(item.getId())).willReturn(item);
        given(itemService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(true);

        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        // When
        itemService.deleteItemById(item.getId());

        // Then
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void deleteItemNotFound() {
        // Given
        Long id = 1L;
        given(itemService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(false);

        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        // When
        Exception ex = assertThrows(ObjectNotFoundException.class, () -> {
            itemService.deleteItemById(id);
        });

        // Then
        assertThat(ex).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void updateItemSuccess() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        Item item = input.mockEntity();
        ItemUpdateData updateData = new ItemUpdateData(
                "updatedName",
                "description",
                new BigDecimal(10),
                10);
        CategoryListData category = new CategoryListData(1L, "mockCategory");
        ItemListData listData = new ItemListData(
                0L,
                updateData.itemName(),
                category,
                updateData.description(),
                updateData.price(),
                updateData.numberInStock());

        given(itemService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(true);
        given(itemRepository.getReferenceById(0L)).willReturn(item);
        given(itemRepository.existsByUserIdAndItemNameIgnoreCase(anyLong(), anyString())).willReturn(false);
        given(itemDTOMapper.apply(item)).willReturn(listData);

        // When
        ItemListData updatedItem = itemService.updateItemById(updateData, item.getId());

        // Then
        assertEquals(updatedItem.itemName(), updateData.itemName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItemNotFound() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        given(itemService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(false);

        ItemUpdateData data = mock(ItemUpdateData.class);

        // When
        Exception ex = assertThrows(ObjectNotFoundException.class, () -> {
            itemService.updateItemById(data, 1L);
        });

        // Then
        assertThat(ex).isInstanceOf(ObjectNotFoundException.class);
    }
}
