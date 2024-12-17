package com.inventory.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.server.auth.dto.AuthLoginData;
import com.inventory.server.category.Category;
import com.inventory.server.category.CategoryRepository;
import com.inventory.server.mocks.MockCategory;
import com.inventory.server.mocks.MockItem;
import com.inventory.server.user.User;
import com.redis.testcontainers.RedisContainer;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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

    @Autowired
    AuthenticationManager manager;

    String token;

    MockItem mockItem = new MockItem();

    MockCategory mockCategory = new MockCategory();

    @Container
    @ServiceConnection
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @Container
    @ServiceConnection
    static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

//    static {
//        mySQLContainer.start();
//    }

//    @BeforeAll
//    static void beforeAll() {
//        mySQLContainer.start();
//    }

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

        // Set authentication object
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginData.username(), loginData.password());
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(manager.authenticate(authToken));
        SecurityContextHolder.setContext(securityContext);
        Object principal = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("USER AUTHENTICATION ID -> " + principal);

        // Save mock entities to container db
        Item item = mockItem.mockEntity();
        Category category = mockCategory.mockEntity();
        item.setCategory(category);
        categoryRepository.save(category);
        itemRepository.save(item);
        categoryRepository.flush();
        itemRepository.flush();

        //Category saveCategory = categoryRepository.findById(1L).get();
        Item saveItem = itemRepository.findByItemNameIgnoreCase(item.getItemName()).get();

        //System.out.println("CATEGORYID -> " + saveCategory.getId());
        System.out.println("ITEMID -> " + saveItem.getId());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void isRunning() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @Test
    void testGetItemsSuccess() throws Exception {
        this.mockMvc.perform(get("/items").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testItemsByCategoryIdSuccess() throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        System.out.println("PRINCIPAL HERE -> " + user.getId());

        this.mockMvc.perform(get("/items/1/category").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token).principal((Principal) principal))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testItemsByCategoryNotFoundException() throws Exception {
        this.mockMvc.perform(get("/items/1646482/category").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://inventory.com/errors/object-does-not-exist"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void testFindItemByIdSuccess() throws Exception {
        this.mockMvc.perform(get("/items/1").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.itemName").value("Name Test0"));
    }
}