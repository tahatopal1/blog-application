package com.project.blogapp.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.project.blogapp.constants.SecurityConstants;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.File;
import com.project.blogapp.entity.User;
import com.project.blogapp.repository.BlogRepository;
import com.project.blogapp.repository.UserRepository;
import com.project.blogapp.service.FileService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.KafkaContainer;
import org.springframework.core.env.Environment;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "s3.purpose=test"
})
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Transactional
@Rollback
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private AmazonS3 s3;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    private static String jwt;
    private final Long blogId = 1L;
    private final String username = "testuser";
    private Blog blog;
    private String dummyContent;
    private File file;
    private User user;

    private static final DockerImageName localstackImage = DockerImageName.parse("localstack/localstack");

    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName
            .parse("confluentinc/cp-kafka:6.2.1")
    );

    @Container
    private static MySQLContainer container = new MySQLContainer("mysql:latest")
            .withDatabaseName("root")
            .withDatabaseName("blog_app_test")
            .withPassword("1234");

    @Container
    private static final LocalStackContainer localstack = new LocalStackContainer(localstackImage)
            .withServices(S3);

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        kafka.start();
        localstack.start();
        container.start();

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", localstack::getSecretKey);
        registry.add("cloud.aws.region.static", localstack::getRegion);
        registry.add("cloud.aws.s3.endpoint", () -> localstack.getEndpoint().toString());

        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", container::getDriverClassName);
        registry.add("spring.flyway.user", container::getUsername);
        registry.add("spring.flyway.password", container::getPassword);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration-test-container");

    }

    @BeforeAll
    public void beforeAll() {
        initialization();
    }

    @BeforeEach
    void setUp() {
        createBucket();
    }

    @AfterAll
    static void afterAll() {
        kafka.stop();
        localstack.stop();
    }

    @Test
    void givenImage_whenUploadFile_thenReturnSuccessful() throws Exception {

        // given
        MockMultipartFile mockImageFile = createDummyFile();

        // when
        this.mockMvc.perform(multipart("/api/blog/{id}/file", blogId)
                        .file(mockImageFile)
                        .param("scale", "1.0")
                        .param("quality", "0.8")
                        .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt))
                .andExpect(status().isOk());

        // then
        blog = blogRepository.findById(blog.getId())
                .orElseThrow(() -> new RuntimeException("No blog record found! Test failed..."));

        file = blog.getFiles()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No file within the blog! Test failed..."));

        assertThat(file.getName()).contains("file");
    }

    @Test
    void givenBlogIDAndFilename_whenDownloadFile_thenReturnFile() throws Exception {

        // given
        setAuth();
        MockMultipartFile mockImageFile = createDummyFile();
        fileService.uploadFile(mockImageFile, blogId, 0.8, 1.2f);

        // then
        blog = blogRepository.findById(blog.getId())
                .orElseThrow(() -> new RuntimeException("No blog record found! Test failed..."));

        file = blog.getFiles()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No file within the blog! Test failed..."));


        this.mockMvc.perform(get("/api/blog/{id}/file/{fileName}", blog.getId(), file.getName())
                        .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void givenBlogIDAndFilename_whenDeleteFile_thenSuccessful() throws Exception {

        // given
        setAuth();
        MockMultipartFile mockImageFile = createDummyFile();
        fileService.uploadFile(mockImageFile, blogId, 0.8, 1.2f);

        // then
        blog = blogRepository.findById(blog.getId())
                .orElseThrow(() -> new RuntimeException("No blog record found! Test failed..."));

        file = blog.getFiles()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No file within the blog! Test failed..."));


        this.mockMvc.perform(delete("/api/blog/{id}/file/{fileName}", blog.getId(), file.getName())
                        .header(SecurityConstants.AUTH_HEADER, "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    private void setAuth() {
        Authentication auth
                = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("GENERIC")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void initialization() {
        createDummyContent();
        buildJWT();
        createUser();
        createBlog();
    }

    private void buildJWT() {
        jwt = Jwts.builder()
                .setIssuer("BlogApp")
                .setSubject("JWT Token")
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 300000000))
                .signWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private void createBucket(){
        s3.createBucket(environment.getProperty("application.bucket.name"));
    }

    private void createBlog() {
        blogRepository.findAll().forEach(blog -> {
            blog.getTags().clear();
            blogRepository.save(blog);
        });
        blogRepository.deleteAll();
        userRepository.deleteAll();

        blog = Blog.builder()
                .title("Blog title 1")
                .content(dummyContent)
                .build();
        blogRepository.save(blog);

        user.getBlogs().add(blog);
        blog.setUser(user);
        userRepository.save(user);

    }

    private void createUser(){
        user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();
        userRepository.save(user);
    }

    private static MockMultipartFile createDummyFile() throws IOException {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = originalImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        MockMultipartFile mockImageFile = new MockMultipartFile("image", "file.jpg", "image/jpeg", imageInByte);
        return mockImageFile;
    }

    private void createDummyContent() {
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range(0, 10)
                .forEach((v) -> stringBuilder.append("Productivity"));
        dummyContent = stringBuilder.toString();
    }

}
