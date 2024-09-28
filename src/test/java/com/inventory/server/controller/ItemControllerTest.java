package com.inventory.server.controller;

import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.dto.item.ItemListData;
import com.inventory.server.mocks.MockItem;
import com.inventory.server.model.Category;
import com.inventory.server.model.Item;
import com.inventory.server.model.User;
import com.inventory.server.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<CreateItemData> createDataDto;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    MockItem input;

    @BeforeEach
    void setUpMocks() {
        input = new MockItem();
    }

//    @Test
//    void detailItemByIdSuccess() throws Exception {
//        // Given
//        Item item = input.mockEntity();
//        ItemListData data = input.mockItemListData();
//
//        SecurityContext securityContextHolder = mock(SecurityContext.class);
//        Authentication authentication = mock(Authentication.class);
//        User user = mock(User.class);
//
//        when(securityContextHolder.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContextHolder);
//        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
//        given(itemRepository.existsByIdAndUserId(any(), any())).willReturn(true);
//
//        given(itemService.detailItemById(0L)).willReturn(data);
//
//        // When and then
//        mvc.perform(
//                get("/items/0/detail").accept(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(0))
//                .andExpect(jsonPath("$.itemName").value("First Name Test0"));
//
//    }

    @Test
    void shouldReturnCode400ToCreateItemRequestWithInvalidData() throws Exception {
        // ARRANGE
        String json = "{}";

        // ACT
        MockHttpServletResponse response = mvc.perform(
                post("/items")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void shouldReturnCode201ToCreateItemRequestWithValidData() throws Exception {
        // ARRANGE
        CreateItemData data = new CreateItemData("Teste 2", "accepted", 2L, new BigDecimal(42), 4);

        // ACT
        MockHttpServletResponse response = mvc.perform(
                post("/items")
                        .content(createDataDto.write(data).getJson())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // ASSERT
        Assertions.assertEquals(201, response.getStatus());
    }
}