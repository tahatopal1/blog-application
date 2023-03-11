package com.project.blogapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.blogapp.constants.SecurityConstants;
import com.project.blogapp.dto.BlogDTO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Transactional
public class BlogControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

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

    // JUnit test for saveBlog REST API
    @Test
    public void givenBlogObject_whenSaveBlog_thenReturn201() throws Exception {

        // given - precondition or setup
        BlogDTO blog = BlogDTO.builder()
                .content("Blog Content")
                .title("Blog Title")
                .build();

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(post("/api/blog")
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blog)));

        // then - verify the output
        response.andDo(print())
                .andExpect(status().isCreated());

    }

    // JUnit test for updateBlogContent REST API
    @Test
    public void givenBlogObject_whenUpdateBlogContent_thenReturn202() throws Exception {

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();
        blogRepository.save(blog);

        BlogDTO blogDTO = BlogDTO.builder()
                .content("Updated Content")
                .title("Updated Title")
                .build();

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(put("/api/blog/{id}", blog.getId())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blogDTO)));

        // then - verify the output
        response.andExpect(status().isAccepted()).andDo(print());

    }

    // JUnit test for updateBlogContent REST API
    @Test
    public void givenBlogObject_whenUpdateBlogContent_thenReturn500() throws Exception {

        // given - precondition or setup
        long blogId = 30L;

        BlogDTO blogDTO = BlogDTO.builder()
                .content("Updated Content")
                .title("Updated Title")
                .build();

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(put("/api/blog/{id}", blogId)
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blogDTO)));

        // then - verify the output
        response.andExpect(status().isInternalServerError()).andDo(print());

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

    // JUnit test for getAllBlogPosts REST API
    @Test
    public void givenListOfBlogs_whenGetAllBlogPosts_thenReturnBlogList() throws Exception {

        // given - precondition or setup
        List<Blog> blogs = new ArrayList<>();
        blogs.add(Blog.builder().content("Blog Content 1").title("Blog Title 1").user(user).build());
        blogs.add(Blog.builder().content("Blog Content 2").title("Blog Title 2").user(user).build());
        blogRepository.saveAll(blogs);

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(get("/api/blog/user/{username}", user.getUsername())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(2)));


    }

    // JUnit test for getAllSummariesByUsername REST API
    @Test
    public void givenListOfBlogs_whenGetAllSummariesByUsername_thenReturnBlogListWithSummaries() throws Exception {

        // given - precondition or setup
        List<Blog> blogs = new ArrayList<>();
        blogs.add(Blog.builder().content("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Saepe doloribus pariatur dolorem quae numquam distinctio aut, voluptatum consequatur ad.")
                .title("Blog Title 1").user(user).build());
        blogs.add(Blog.builder().content("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Saepe doloribus pariatur dolorem quae numquam distinctio aut, voluptatum consequatur ad.")
                .title("Blog Title 2").user(user).build());
        blogRepository.saveAll(blogs);

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(get("/api/blog/summaries/{username}", user.getUsername())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(2)));


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

    // JUnit test for getBlog REST API
    @Test
    public void givenBlogObject_whenGetBlog_Then() throws Exception {

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();
        blogRepository.save(blog);

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(get("/api/blog/{id}", blog.getId())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title", is(blog.getTitle())))
                .andExpect(jsonPath("$.content", is(blog.getContent())));


    }

    // JUnit test for deleteBlog
    @Test
    public void givenBlogObject_whenDeleteBlog_thenReturn200() throws Exception {

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();
        blogRepository.save(blog);

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(delete("/api/blog/{id}", blog.getId())
                .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the output
        response.andExpect(status().isOk()).andDo(print());


    }


}
