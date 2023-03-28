package com.project.blogapp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.File;
import com.project.blogapp.repository.*;
import com.project.blogapp.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class FileServiceImpl implements FileService {

    private BlogRepository blogRepository;
    private UserService userService;
    private AmazonS3 s3Client;
    private FileUtils fileUtils;
    private Environment environment;

    @Override
    public void uploadFile(MultipartFile file, Long blogId, Double scale, Float quality) throws Exception {

        String username = userService.getUsernameFromContextHolder();
        Optional<Blog> blogOptional = blogRepository.getBlogByIdAndUsername(blogId, username);

        if (blogOptional.isEmpty()) {
            throw new RuntimeException("There's no such blog with id: " + blogId);
        }

        String bucketName = environment.getProperty("application.bucket.name");

        java.io.File convertedFile = fileUtils.saveImage(file, scale, quality);
        s3Client.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), convertedFile));

        buildFileAndSave(file, blogOptional);
    }

    @Override
    public byte[] downloadFile(Long id, String fileName) throws IOException {
        String username = userService.getUsernameFromContextHolder();
        String bucketName = environment.getProperty("application.bucket.name");

        Optional<Blog> blogOptional = blogRepository.getBlogByIdAndUsername(id, username);
        if(blogOptional.isEmpty()){
            throw new RuntimeException("There's no such blog with id: " + id);
        }

        Optional<File> fileOptional = blogOptional.get()
                .getFiles()
                .stream()
                .filter(fl -> fl.getName().equals(fileName))
                .findFirst();
        if (fileOptional.isEmpty()){
            throw new RuntimeException("There's no such file with name: " + fileName);
        }

        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return bytes;
    }

    @Override
    public void deleteFile(Long id, String fileName) {
        String username = userService.getUsernameFromContextHolder();
        Optional<Blog> blogOptional = blogRepository.getBlogByIdAndUsername(id, username);

        if (blogOptional.isEmpty()) {
            throw new RuntimeException("There's no such blog with id: " + id);
        }

        String bucketName = environment.getProperty("application.bucket.name");
        s3Client.deleteObject(bucketName, fileName);
    }

    private void buildFileAndSave(MultipartFile file, Optional<Blog> blogOptional) {
        File f = File.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType()).build();

        Blog blog = blogOptional.get();
        blog.getFiles().add(f);
        blogRepository.save(blog);
    }

}
