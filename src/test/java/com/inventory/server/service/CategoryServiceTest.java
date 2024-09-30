package com.inventory.server.service;

import com.inventory.server.domain.CategoryRepository;
import com.inventory.server.dto.category.CategoryCreateMapper;
import com.inventory.server.dto.category.CategoryDTOMapper;
import com.inventory.server.dto.category.CategoryListData;
import com.inventory.server.dto.category.CreateCategoryData;
import com.inventory.server.dto.item.ItemListData;
import com.inventory.server.dto.item.ItemUpdateData;
import com.inventory.server.infra.exception.CategoryAlreadyCreatedException;
import com.inventory.server.infra.exception.CategoryNotFoundException;
import com.inventory.server.mocks.MockCategory;
import com.inventory.server.model.Category;
import com.inventory.server.model.Item;
import com.inventory.server.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    MockCategory input;

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    CategoryDTOMapper categoryDTOMapper;

    @Mock
    CategoryCreateMapper categoryCreateMapper;

    @Mock
    UriComponentsBuilder uriBuilder;

    @Mock
    UriComponents uriComponents;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @BeforeEach
    void setUpMocks() {
        input = new MockCategory();
    }

    @Test
    void findCategoryByIdSuccess() {
        // Given
        Category category = input.mockEntity();
        CategoryListData categoryListData = input.mockCategoryListData();

        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));
        given(categoryDTOMapper.apply(category)).willReturn(categoryListData);

        // When
        CategoryListData result = categoryService.listCategoryById(category.getId());

        // Then
        assertEquals(categoryListData, result);
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryDTOMapper, times(1)).apply(category);
    }

    @Test
    void findCategoryByIdNotFound() {
        // Given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        Exception ex = assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.listCategoryById(anyLong());
        });

        // Then
        assertThat(ex).isInstanceOf(CategoryNotFoundException.class).hasMessage("Category not found");
    }

    @Test
    void saveCategorySuccess() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        CreateCategoryData data = input.mockCreateCategoryData();

        given(uriBuilder.path(stringCaptor.capture())).willReturn(uriBuilder);
        given(uriBuilder.buildAndExpand(longCaptor.capture())).willReturn(uriComponents);
        given(categoryCreateMapper.apply(any(Category.class))).willReturn(data);

        // When
        categoryService.registerCategory(data, uriBuilder);

        // Then
        then(categoryRepository).should().save(categoryCaptor.capture());
        Category category = categoryCaptor.getValue();
        assertEquals(category.getCategoryName(), data.categoryName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void saveCategoryNameInUse() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        CreateCategoryData data = input.mockCreateCategoryData();

        given(categoryRepository.existsByUserIdAndCategoryNameIgnoreCase(anyLong(), anyString())).willReturn(true);

        // When
        Exception ex = assertThrows(CategoryAlreadyCreatedException.class, () -> {
            categoryService.registerCategory(data, uriBuilder);
        });

        // Then
        assertThat(ex).isInstanceOf(CategoryAlreadyCreatedException.class).hasMessage("There is a category created with this name");
    }

    @Test
    void deleteCategorySuccess() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        Category category = input.mockEntity();
        given(categoryRepository.getReferenceById(category.getId())).willReturn(category);
        given(categoryService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(true);

        // When
        categoryService.deleteCategoryById(category.getId());

        // Then
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategoryNotFound() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        Category category = input.mockEntity();
        //given(categoryRepository.getReferenceById(category.getId())).willReturn(category);
        given(categoryService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(false);

        // When
        Exception ex = assertThrows(CategoryNotFoundException.class, () -> {
                    categoryService.deleteCategoryById(category.getId());
        });

        // Then
        assertThat(ex).isInstanceOf(CategoryNotFoundException.class).hasMessage("Category not found");
    }

    @Test
    void updateCategorySuccess() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        Category category = input.mockEntity();
        CreateCategoryData data = input.mockCreateCategoryData();

        given(categoryService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(true);
        given(categoryRepository.getReferenceById(0L)).willReturn(category);
        given(categoryRepository.existsByUserIdAndCategoryNameIgnoreCase(anyLong(), anyString())).willReturn(false);
        given(categoryCreateMapper.apply(category)).willReturn(data);

        // When
        CreateCategoryData updatedItem = categoryService.updateCategory(category.getId(), data);

        // Then
        assertEquals(updatedItem.categoryName(), data.categoryName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategoryNotFound() {
        // Given
        SecurityContext securityContextHolder = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        User user = mock(User.class);

        given(securityContextHolder.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContextHolder);
        given(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).willReturn(user);

        CreateCategoryData data = mock(CreateCategoryData.class);

        given(categoryService.existsByIdAndUserId(anyLong(), anyLong())).willReturn(false);

        // When
        Exception ex = assertThrows(CategoryNotFoundException.class,
                () -> {
                    categoryService.updateCategory(1L, data);
        });

        // Then
        assertThat(ex).isInstanceOf(CategoryNotFoundException.class).hasMessage("Category not found");
    }
}