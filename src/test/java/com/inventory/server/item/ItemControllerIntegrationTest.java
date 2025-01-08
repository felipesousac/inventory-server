package com.inventory.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.server.auth.dto.AuthLoginData;
import com.inventory.server.category.Category;
import com.inventory.server.category.CategoryRepository;
import com.inventory.server.item.dto.CreateItemData;
import com.inventory.server.item.dto.ItemUpdateData;
import com.inventory.server.mocks.MockCategory;
import com.inventory.server.mocks.MockItem;
import com.redis.testcontainers.RedisContainer;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CategoryRepository categoryRepository;

    String token;

    MockItem itemFactory = new MockItem();

    MockCategory categoryFactory = new MockCategory();

    Item item;

    Category category;

    String URL_PATH = "/items";

    @Container
    @ServiceConnection
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @Container
    @ServiceConnection
    static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

    @BeforeEach
    @Transactional
    void setUp() throws Exception {
        // Get token from /auth endpoint
        AuthLoginData loginData = new AuthLoginData("admin", "admin");
        String json = objectMapper.writeValueAsString(loginData);

        ResultActions resultActions =
                mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);
        this.token = "Bearer " + jsonObject.getString("token");

        // Save mock entities to container db
        this.category = categoryFactory.mockEntity();
        categoryRepository.save(category);

        this.item = itemFactory.mockEntity();
        this.item.setCategory(category);
        itemRepository.save(item);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void isDbContainerRunning() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @Test
    void testGetItemsSuccess() throws Exception {
        this.mockMvc.perform(get(this.URL_PATH).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,
                        this.token))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testItemsByCategoryIdSuccess() throws Exception {
        Long categoryId = this.category.getId();

        this.mockMvc.perform(get(this.URL_PATH + "/" + categoryId + "/category")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testItemsByCategoryNotFound() throws Exception {
        this.mockMvc.perform(get(this.URL_PATH + "/1646482/category").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://inventory.com/errors/object-does-not-exist"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testFindItemByIdSuccess() throws Exception {
        Long itemId = this.item.getId();

        this.mockMvc.perform(get(this.URL_PATH + "/" + itemId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.itemName").value(this.item.getItemName()));
    }

    @Test
    void testFindItemByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.URL_PATH + "/1222").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://inventory.com/errors/object-does-not-exist"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testCreateItemSuccess() throws Exception {
        CreateItemData data = new CreateItemData(
                "Create Item Success",
                "Name Description",
                this.category.getId(),
                BigDecimal.valueOf(100),
                10);

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(this.URL_PATH)
                .header(HttpHeaders.AUTHORIZATION, this.token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.itemName").value(data.itemName()))
                .andExpect(jsonPath("$.description").value(data.description()))
                .andExpect(jsonPath("$.category.id").value(data.categoryId()));
    }

    @Test
    void testCreateItemWithInvalidFields() throws Exception {
        CreateItemData data = new CreateItemData(
                "",
                "",
                this.category.getId(),
                BigDecimal.valueOf(100),
                10);

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(this.URL_PATH)
                .header(HttpHeaders.AUTHORIZATION, this.token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.title").value("One or more fields are invalid"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields").isNotEmpty());
    }

    @Test
    void testCreateItemThatAlreadyExists() throws Exception {
        CreateItemData data = new CreateItemData(
                this.item.getItemName(),
                this.item.getDescription(),
                this.category.getId(),
                BigDecimal.valueOf(100),
                10);

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(this.URL_PATH)
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("There is an object created with this name: " + data.itemName()))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void testCreateItemWithNotFoundCategory() throws Exception {
        CreateItemData data = new CreateItemData(
                "Create Item Success",
                "Name Description",
                111123L,
                BigDecimal.valueOf(100),
                10);

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(this.URL_PATH)
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category with id " + data.categoryId() + " not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateItemSuccess() throws Exception {
        ItemUpdateData data = new ItemUpdateData(
                "Updated",
                "Description",
                new BigDecimal(10),
                123
        );

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(put(this.URL_PATH + "/" + this.item.getId())
                .header(HttpHeaders.AUTHORIZATION, this.token)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.itemName").value("Updated"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void updateItemWithNonExistingItem() throws Exception {
        ItemUpdateData data = new ItemUpdateData(
                "Updated",
                "Description",
                new BigDecimal(10),
                123
        );

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(put(this.URL_PATH + "/112334")
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Item with id 112334 not found"));
    }

    @Test
    void deleteItemSuccess() throws Exception {
        this.mockMvc.perform(delete(this.URL_PATH + "/" + this.item.getId())
                .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteItemThatDoNotExist() throws Exception {
        this.mockMvc.perform(delete(this.URL_PATH + "/213415")
                .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Item with id 213415 not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findActiveAndDeletedItemsSuccess() throws Exception {
        long count = itemRepository.count();

        this.mockMvc.perform(get(this.URL_PATH + "/admin/1")
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").value(count))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void findActiveAndDeletedItemsWithNonAdminToken() throws Exception {
        AuthLoginData loginData = new AuthLoginData("user", "user");
        String json = objectMapper.writeValueAsString(loginData);

        ResultActions resultActions =
                mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);
        this.token = "Bearer " + jsonObject.getString("token");

        this.mockMvc.perform(get(this.URL_PATH + "/admin/1")
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Access Denied"))
                .andExpect(jsonPath("$.status").value(403));
    }
}