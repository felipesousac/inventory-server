package com.inventory.server.controller;

import com.inventory.server.domain.ItemRepository;
import com.inventory.server.dto.item.CreateItemData;
import com.inventory.server.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    private JacksonTester<CreateItemData> createDataDto;

    @MockBean
    private ItemService itemService;

    //@MockBean
    //private ItemRepository itemRepository;

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