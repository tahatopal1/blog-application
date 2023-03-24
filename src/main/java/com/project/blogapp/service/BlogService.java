package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BlogService {

    void saveBlog(BlogDTO blogDTO);

    List<BlogDTO> getAllBlogPostByUsernameWithSummaries(String username);

    List<BlogDTO> getAllBlogPostsByUsername(String username);

    void updateBlog(Long blogId, BlogDTO blogDTO);

    List<BlogDTO> getAllBlogPostsByTag(Long id);

    void addTag(Long blogId, Long tagId);

    void discardTag(Long blogId, Long tagId);

    BlogDTO getBlogById(Long id);

    void deleteBlogById(Long id);

    void uploadFile(MultipartFile file, Long blogId, Double scale, Float quality) throws Exception;

    byte[] downloadFile(Long id, String fileName) throws IOException;

    void deleteFile(Long id, String fileName);

}
