package com.project.blogapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.blogapp.constants.SecurityConstants;
import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.entity.User;
import com.project.blogapp.repository.UserRepository;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Transactional
public class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static MySQLContainer container = new MySQLContainer("mysql:latest")
            .withDatabaseName("root")
            .withDatabaseName("blog_app_test")
            .withPassword("1234");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }

    // JUnit test for login REST API
    @Test
    public void givenUserBasic_whenAuthenticateUser_thenReturnJWTOnHeader() throws Exception {

        // given - precondition or setup
        User user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();
        userRepository.save(user);

        String originalInput = "testuser:password";
        Base64 base64 = new Base64();
        String encodedString = new String(base64.encode(originalInput.getBytes()));

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(post("/login")
                 .servletPath("/login")
                .header(SecurityConstants.AUTH_HEADER, "Basic " + encodedString));

        // then - verify the output
        response.andDo(print())
                .andExpect(result -> result.getResponse()
                        .getHeaderNames()
                        .stream()
                        .anyMatch(s -> s.equals(SecurityConstants.AUTH_HEADER)))
                .andExpect(status().isOk());

    }

    // JUnit test for signup REST API
    @Test
    public void givenNothing_whenRegisterUser_thenSuccessful() throws Exception {

        // when - action or the behaviour that we are going to test
        UserDTO user = UserDTO.builder()
                .username("testinguser")
                .password("password")
                .displayName("testinguser")
                .build();

        ResultActions response = mvc.perform(post("/signup")
                .servletPath("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // then - verify the output
        response.andDo(print())
                .andExpect(status().isAccepted());

    }

}
