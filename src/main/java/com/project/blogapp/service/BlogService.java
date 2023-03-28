package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BlogService {

    void saveBlog(BlogDTO blogDTO);

    List<BlogDTO> getAllBlogPostByUsernameWithSummaries(String username, Pageable pageable);

    List<BlogDTO> getAllBlogPostsByUsername(String username);

    void updateBlog(Long blogId, BlogDTO blogDTO);

    BlogDTO getBlogById(String username, Long id);

    void deleteBlogById(Long id);

}
