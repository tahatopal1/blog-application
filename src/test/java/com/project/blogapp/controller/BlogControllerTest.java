package com.project.blogapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.blogapp.constants.SecurityConstants;
import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.User;
import com.project.blogapp.repository.BlogRepository;
import com.project.blogapp.repository.TagRepository;
import com.project.blogapp.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
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
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.jpa.properties.hibernate.search.enabled=true"})
@AutoConfigureMockMvc
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

//    @Container
//    private static final ElasticsearchContainer elasticsearch = new ElasticsearchContainer(DockerImageName
//            .parse("docker.elastic.co/elasticsearch/elasticsearch:7.4.2")
//    );

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
//        elasticsearch.start();
//        registry.add("spring.jpa.properties.hibernate.search.backend.hosts", elasticsearch::getHttpHostAddress);
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

//    @AfterAll
//    static void afterAll() {
//        elasticsearch.stop();
//    }

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

    // JUnit test for getBlog REST API
    @Test
    public void givenBlogObject_whenGetBlog_ThenReturnBlogObject() throws Exception {

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();
        blogRepository.save(blog);

        // when - action or the behaviour that we are going to test
        ResultActions response = mvc.perform(get("/api/blog/user/{username}/{id}",
                                                                 user.getUsername(), blog.getId())
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
        response.andExpect(status().isAccepted()).andDo(print());

    }

    @Test
    @Disabled
    void givenSearchText_whenSearchBlogs_thenReturnBlogList() throws Exception {

        saveBlogsBulkOperation();
        String searchText = "Dummy";

        mvc.perform(get("/api/blog/search", user.getUsername())
                        .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("searchText", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(10)));

    }

    private void saveBlogsBulkOperation() {
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range(0, 10)
                .forEach((v) -> stringBuilder.append("Productivity"));
        String dummyContent = stringBuilder.toString();

        Set<Blog> blogCollection = IntStream.range(3, 13)
                .mapToObj(value -> {
                    Blog blog = Blog.builder()
                            .title("Dummy Title " + value)
                            .content(dummyContent)
                            .build();
                    return blog;
                })
                .collect(Collectors.toSet());
        blogRepository.saveAll(blogCollection);
    }

}
