package com.inventory.server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.server.user.dto.UserRegisterData;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    String token;

    String URL_PATH = "/users";

    @Container
    @ServiceConnection
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @Container
    @ServiceConnection
    static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void isDbContainerRunning() {
        assertThat(mySQLContainer.isCreated()).isTrue();
        assertThat(mySQLContainer.isRunning()).isTrue();
    }

    @Test
    void createUserSuccess() throws Exception {
        UserRegisterData data = new UserRegisterData(
                "username",
                "password",
                "password"
        );

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(URL_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void createUserWithInvalidFields() throws Exception {
        UserRegisterData data = new UserRegisterData("", "", "");

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(URL_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("One or more fields are invalid"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createUserAlreadyExists() throws Exception {
        UserRegisterData data = new UserRegisterData(
                "existingName",
                "password",
                "password"
        );

        User user = new User(data);
        userRepository.save(user);

        String json = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post(URL_PATH).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("User "+ data.username() +" already exists"))
                .andExpect(jsonPath("$.status").value(409));
    }
}