package com.project.blogapp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.File;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.entity.User;
import com.project.blogapp.mapper.blog.BlogDTOToBlogMapper;
import com.project.blogapp.mapper.blog.BlogToBlogDTOMapper;
import com.project.blogapp.repository.*;
import com.project.blogapp.util.FileUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    private TagRepository tagRepository;
    private BlogDTOToBlogMapper blogDTOToBlogMapper;
    private BlogToBlogDTOMapper blogToBlogDTOMapper;
    private UserService userService;
    private UserRepository userRepository;
    private FileRepository imageRepository;

    private AmazonS3 s3Client;

    private FileUtils fileUtils;
    private Environment environment;

    @Override
    public void saveBlog(BlogDTO blogDTO) {
        log.info("{} - saveBlog method is working", this.getClass().getSimpleName());
        User user = userService.getUserFromContextHolder();
        Blog blog = blogDTOToBlogMapper.map(blogDTO);
        user.getBlogs().add(blog);
        blog.setUser(user);
        userRepository.save(user);
    }

    @Override
    public List<BlogDTO> getAllBlogPostByUsernameWithSummaries(String username) {
        log.info("{} - getAllBlogPostByUsernameWithSummaries method is working", this.getClass().getSimpleName());
        List<Blog> blogs = blogRepository.getAllByUsername(username);
        return blogs.stream().map(blogToBlogDTOMapper::mapWithSummary).collect(Collectors.toList());
    }

    @Override
    public List<BlogDTO> getAllBlogPostsByUsername(String username) {
        log.info("{} - getAllBlogPostsByUsername method is working", this.getClass().getSimpleName());
        return blogRepository.getAllByUsername(username)
                .stream()
                .map(blogToBlogDTOMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public void updateBlog(Long blogId, BlogDTO blogDTO) {
        log.info("{} - updateBlog method is working", this.getClass().getSimpleName());
        String username = userService.getUsernameFromContextHolder();
        Optional<Blog> optionalBlog = blogRepository.getBlogByIdAndUsername(blogId, username);
        if (optionalBlog.isEmpty()) {
            log.error("{} - Blog with id is null: {}", this.getClass().getSimpleName(), blogId);
            throw new RuntimeException("Blog with id is null: " + blogId);
        }
        Blog blog = optionalBlog.get();
        blog.setContent(blogDTO.getContent());
        blog.setTitle(blogDTO.getTitle());
        blogRepository.save(blog);
    }

    @Override
    public List<BlogDTO> getAllBlogPostsByTag(Long id) {
        log.info("{} - getAllBlogPostsByTag method is working", this.getClass().getSimpleName());
        Optional<Tag> tag = tagRepository.findById(id);
        if (tag.isEmpty()) {
            log.error("{} - Tag with id is null: {}", this.getClass().getSimpleName(), id);
            throw new RuntimeException("Tag with id is null: " + id);
        }
        List<Blog> blogs = blogRepository.getAllBlogsByTagId(id);
        return blogs.stream().map(blogToBlogDTOMapper::map).collect(Collectors.toList());
    }

    @Override
    public void addTag(Long blogId, Long tagId) {
        log.info("{} - addTag method is working", this.getClass().getSimpleName());
        String username = userService.getUsernameFromContextHolder();
        blogRepository.getBlogByIdAndUsername(blogId, username).ifPresentOrElse(blog -> {
            tagRepository.findById(tagId).ifPresentOrElse(tag -> {
                blog.getTags().add(tag);
                blogRepository.save(blog);
            }, () -> {
                log.error("{} - Tag with id is null: {}", this.getClass().getSimpleName(), tagId);
                throw new RuntimeException("Tag with id not null: " + blogId);
            });
        }, () -> {
            log.error("{} - Blog with id is null: {}", this.getClass().getSimpleName(), blogId);
            throw new RuntimeException("Blog with is not null: " + tagId);
        });
    }

    @Override
    public void discardTag(Long blogId, Long tagId) {
        log.info("{} - discardTag method is working", this.getClass().getSimpleName());
        String username = userService.getUsernameFromContextHolder();
        blogRepository.getBlogByIdAndUsername(blogId, username).ifPresentOrElse(blog -> {
            tagRepository.findById(tagId).ifPresentOrElse(tag -> {
                blog.getTags().remove(tag);
                blogRepository.save(blog);
            }, () -> {
                log.error("{} - Tag with id is null: {}", this.getClass().getSimpleName(), tagId);
                throw new RuntimeException("Tag with id is null: " + blogId);
            });
        }, () -> {
            log.error("{} - Blog with id is null: {}", this.getClass().getSimpleName(), blogId);
            throw new RuntimeException("Blog with id is null: " + tagId);
        });
    }

    @Override
    public BlogDTO getBlogById(Long id) {
        log.info("{} - getBlogById method is working", this.getClass().getSimpleName());
        Blog blog = blogRepository.findById(id).orElseThrow(() -> {
            log.error("{} - There's no such blog with id: {}", this.getClass().getSimpleName(), id);
            throw new RuntimeException("There's no such blog with id: " + id);
        });
        return blogToBlogDTOMapper.map(blog);
    }

    @Override
    public void deleteBlogById(Long id) {
        log.info("{} - deleteBlogById method is working", this.getClass().getSimpleName());
        String username = userService.getUsernameFromContextHolder();
        blogRepository.getBlogByIdAndUsername(id, username).ifPresentOrElse(blog -> blogRepository.deleteById(id), () -> {
            log.error("{} - There's no such blog with id: {}", this.getClass().getSimpleName(), id);
            throw new RuntimeException("There's no such blog with id: " + id);
        });
    }

    @Override
    public void uploadFile(MultipartFile file, Long blogId, Double scale, Float quality) throws Exception {

        String username = userService.getUsernameFromContextHolder();
        Optional<Blog> blogOptional = blogRepository.getBlogByIdAndUsername(blogId, username);

        if (blogOptional.isEmpty()) {
            throw new RuntimeException("There's no such blog with id: " + blogId);
        }

        String bucketName = environment.getProperty("application.bucket.name");

        java.io.File convertedFile = fileUtils.saveImage(file, scale, quality, bucketName);
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
                .getImages()
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
        blog.getImages().add(f);
        blogRepository.save(blog);
    }


}
