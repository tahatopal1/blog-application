package com.project.blogapp.controller;

import com.project.blogapp.constants.SecurityConstants;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.entity.User;
import com.project.blogapp.repository.BlogRepository;
import com.project.blogapp.repository.TagRepository;
import com.project.blogapp.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class TagControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private String jwt;

    @Container
    private static MySQLContainer container = new MySQLContainer("mysql:latest")
            .withDatabaseName("root")
            .withDatabaseName("blog_app_test")
            .withPassword("1234");

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", container::getDriverClassName);
        registry.add("spring.flyway.user", container::getUsername);
        registry.add("spring.flyway.password", container::getPassword);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration-test-container");
    }

    @BeforeEach
    void setUp() {
        blogRepository.findAll().forEach(blog -> {
            blog.getTags().clear();
            blogRepository.save(blog);
        });
        tagRepository.deleteAll();
        blogRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();

        userRepository.save(user);

        jwt = Jwts.builder()
                .setIssuer("BlogApp")
                .setSubject("JWT Token")
                .claim("username", "testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 300000000))
                .signWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    // JUnit test for addTag REST API
    @Test
    public void givenTagAndBlogObjects_whenAddTag_thenReturn202() throws Exception {

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();
        blogRepository.save(blog);

        Tag tag = Tag.builder()
                .tag_name("Generic Tag")
                .build();
        tagRepository.save(tag);

        blog.getTags().add(tag);
        blogRepository.save(blog);

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(put("/api/blog/{id}/tag/{tagId}", blog.getId(), tag.getId())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        response.andExpect(status().isAccepted()).andDo(print());

    }

    // JUnit test for discardTag REST API
    @Test
    public void givenTagAndBlogObjects_whenDiscardTag_thenReturn202() throws Exception {

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();
        blogRepository.save(blog);

        Tag tag = Tag.builder()
                .tag_name("Generic Tag")
                .build();
        tagRepository.save(tag);

        blog.getTags().add(tag);
        blogRepository.save(blog);

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(delete("/api/blog/{id}/tag/{tagId}", blog.getId(), tag.getId())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        response.andExpect(status().isAccepted()).andDo(print());

    }

    // JUnit test for getAllBlogPostsByTags REST API
    @Test
    public void givenTagAndBlogObjects_whenGetAllBlogPostsByTags_thenReturnBlogList() throws Exception {

        // given - precondition or setup
        List<Blog> blogs = new ArrayList<>();
        blogs.add(Blog.builder().content("Blog Content 1").title("Blog Title 1").user(user).build());
        blogs.add(Blog.builder().content("Blog Content 2").title("Blog Title 2").user(user).build());
        blogRepository.saveAll(blogs);

        Tag tag = Tag.builder()
                .tag_name("Generic Tag")
                .build();
        tagRepository.save(tag);

        blogRepository.findAll().forEach(blog -> {
            blog.getTags().add(tag);
            blogRepository.save(blog);
        });

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(get("/api/blog/tag/{id}", tag.getId())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(2)));


    }

}
