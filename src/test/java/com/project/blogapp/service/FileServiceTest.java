package com.project.blogapp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.repository.BlogRepository;
import com.project.blogapp.util.FileUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private BlogRepository blogRepository;
    @Mock
    private UserService userService;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private FileUtils fileUtils;
    @Mock
    private Environment environment;
    @InjectMocks
    private FileServiceImpl fileService;

    private String username = "username";

    @BeforeEach
    void beforeAll() {
        given(userService.getUsernameFromContextHolder()).willReturn(username);
    }

    @SneakyThrows
    @Test
    void givenFile_whenSaveFile_thenSuccess() {

        // given - precondition or setup
        String bucketName = "Bucket";
        MultipartFile file = mock(MultipartFile.class);
        Long blogId = 1L;
        Double scale = 0.1;
        Float quality = 0.1f;
        Blog blog = Blog.builder().build();
        File outputFile = File.createTempFile("test", ".tmp");


        given(blogRepository.getBlogByIdAndUsername(blogId, username)).willReturn(Optional.of(blog));
        given(environment.getProperty("application.bucket.name")).willReturn(bucketName);
        given(fileUtils.saveImage(file, scale, quality)).willReturn(outputFile);

        // when - action or the behaviour that we are going to test
        fileService.uploadFile(file, blogId, scale, quality);

        // then - verify the output
        verify(s3Client).putObject(any(PutObjectRequest.class));
        verify(blogRepository, times(1)).save(blog);

    }

    @SneakyThrows
    @Test
    void givenFile_whenSaveFile_thenError() {

        // given - precondition or setup
        MultipartFile file = mock(MultipartFile.class);
        Long blogId = 1L;
        Double scale = 0.1;
        Float quality = 0.1f;
        Blog blog = Blog.builder().build();

        given(blogRepository.getBlogByIdAndUsername(blogId, username)).willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> fileService.uploadFile(file, blogId, scale, quality));

        // then - verify the output
        verify(blogRepository, never()).save(blog);

    }

    @SneakyThrows
    @Test
    void givenIdAndName_whenDownloadFile_thenReturnByteArray(){

        // given - precondition or setup
        Long id = 1L;
        String fileName = "test.txt";
        String bucketName = "Bucket";
        String content = "test content";
        Blog blog = Blog.builder().build();
        com.project.blogapp.entity.File file = com.project.blogapp.entity.File.builder()
                .id(id)
                .type("text/plain")
                .name(fileName)
                .build();
        blog.getFiles().add(file);

        S3Object s3Object = new S3Object();
        S3ObjectInputStream inputStream = new S3ObjectInputStream(new ByteArrayInputStream(content.getBytes()), null);
        s3Object.setObjectContent(inputStream);


        // Stub method calls
        given(environment.getProperty("application.bucket.name")).willReturn(bucketName);
        given(blogRepository.getBlogByIdAndUsername(id, username)).willReturn(Optional.of(blog));
        given(s3Client.getObject(bucketName, fileName)).willReturn(s3Object);

        // when - action or the behaviour that we are going to test
        byte[] bytes = fileService.downloadFile(id, fileName);

        // then - verify the output
        assertNotNull(bytes);
        assertArrayEquals(content.getBytes(), bytes);

    }

    @Test
    public void givenIdAndName_whenDownloadFile_thenReturnNoSuchBlogError() {

        // given - precondition or setup
        Long id = 1L;
        String fileName = "test.txt";
        String username = "testUser";

        // Stub method calls
        given(blogRepository.getBlogByIdAndUsername(id, username)).willReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> fileService.downloadFile(id, fileName),
                "There's no such blog with id: " + id);
    }

    @Test
    public void givenIdAndName_whenDownloadFile_thenReturnNoSuchFileError() {

        // given - precondition or setup
        Long id = 1L;
        String fileName = "test.txt";
        Blog blog = Blog.builder().build();

        // Stub method calls
        given(blogRepository.getBlogByIdAndUsername(id, username)).willReturn(Optional.of(blog));

        // When / Then
        assertThrows(RuntimeException.class, () -> fileService.downloadFile(id, fileName),
                "There's no such file with name: " + fileName);
    }

    @Test
    void givenIdAndName_whenDeleteFile_thenSuccess() {

        // given - precondition or setup
        Long id = 1L;
        String fileName = "test.txt";
        String bucketName = "Bucket";
        Blog blog = Blog.builder().build();

        given(blogRepository.getBlogByIdAndUsername(id, username)).willReturn(Optional.of(blog));
        given(environment.getProperty("application.bucket.name")).willReturn(bucketName);

        // when - action or the behaviour that we are going to test
        fileService.deleteFile(id, fileName);

        verify(s3Client, times(1)).deleteObject(bucketName, fileName);

    }

    @Test
    void givenIdAndName_whenDeleteFile_thenError() {

        // given - precondition or setup
        Long id = 1L;
        String fileName = "test.txt";
        String bucketName = "Bucket";
        Blog blog = Blog.builder().build();

        given(blogRepository.getBlogByIdAndUsername(id, username)).willReturn(Optional.empty());

        // When / Then
        assertThrows(RuntimeException.class, () -> fileService.deleteFile(id, fileName),
                "There's no such blog with id: " + id);

        verify(s3Client, never()).deleteObject(bucketName, fileName);

    }

}
