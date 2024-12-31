package com.inventory.server.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.server.auth.dto.AuthLoginData;
import com.inventory.server.category.dto.CreateCategoryData;
import com.inventory.server.mocks.MockCategory;
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


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CategoryRepository categoryRepository;

    String token;

    MockCategory categoryFactory = new MockCategory();

    Category category;

    String URL_PATH = "/categories";

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

        // Save mock entity to container db
        this.category = categoryFactory.mockEntity();
        categoryRepository.save(category);
    }

    @AfterEach
    void afterEach() {
        categoryRepository.deleteAll();
    }

    @Test
    void isDbContainerRunning() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @Test
    void testGetCategoriesSuccess() throws Exception {
        this.mockMvc.perform(get(this.URL_PATH).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,
                        this.token))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testFindCategoryIdSuccess() throws Exception {
        Long categoryId = this.category.getId();

        this.mockMvc.perform(get(this.URL_PATH + "/" + categoryId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.categoryName").value(this.category.getCategoryName()));
    }

    @Test
    void testFindCategoryByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.URL_PATH + "/1222").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category with id 1222 not found"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testCreateCategorySuccess() throws Exception {
        CreateCategoryData data = new CreateCategoryData(
                "Create Category Success",
                "Category Description"
        );

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(this.URL_PATH)
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.categoryName").value(data.categoryName()))
                .andExpect(jsonPath("$.description").value(data.description()));
    }

    @Test
    void testCreateCategoryWithInvalidFields() throws Exception {
        CreateCategoryData data = new CreateCategoryData(
                "",
                ""
        );

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
    void testCreateCategoryThatAlreadyExists() throws Exception {
        CreateCategoryData data = new CreateCategoryData(
                this.category.getCategoryName(),
                this.category.getDescription()
        );

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(this.URL_PATH)
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.title").value("There is an object created with this name: " + data.categoryName()))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateCategorySuccess() throws Exception {
        CreateCategoryData data = new CreateCategoryData(
                "Updated",
                "Description"
        );

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(put(this.URL_PATH + "/" + this.category.getId())
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.categoryName").value("Updated"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void updateCategoryWithNonExistingItem() throws Exception {
        CreateCategoryData data = new CreateCategoryData(
                "Updated",
                "Description"
        );

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(put(this.URL_PATH + "/112334")
                        .header(HttpHeaders.AUTHORIZATION, this.token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Category with id 112334 not found"));
    }

    @Test
    void deleteCategorySuccess() throws Exception {
        this.mockMvc.perform(delete(this.URL_PATH + "/" + this.category.getId())
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategoryThatDoNotExist() throws Exception {
        this.mockMvc.perform(delete(this.URL_PATH + "/213415")
                        .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category with id 213415 not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findActiveAndDeletedCategoriesSuccess() throws Exception {
        long count = categoryRepository.count();

        this.mockMvc.perform(get(this.URL_PATH + "/admin/1")
                .header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").value(count))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void findActiveAndDeletedCategoriesWithNonAdminToken() throws Exception {
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