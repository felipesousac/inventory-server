package com.inventory.server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.server.auth.dto.AuthLoginData;
import com.redis.testcontainers.RedisContainer;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ItemControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    String token;

    @Container
    @ServiceConnection
    static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

    @BeforeEach
    void setUp() throws Exception {
        AuthLoginData loginData = new AuthLoginData("admin", "admin");
        String json = objectMapper.writeValueAsString(loginData);

        ResultActions resultActions =
                mockMvc.perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(contentAsString);
        this.token = "Bearer " + jsonObject.getString("token");
    }

    @Test
    void testGetItemsSuccess() throws Exception {
        this.mockMvc.perform(get("/items").accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, this.token))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(status().is2xxSuccessful());
    }

//    @Test
//    void testItemsByCategoryIdSuccess() {
//        this.mockMvc.perform(get("/items/1/category"))
//    }
}