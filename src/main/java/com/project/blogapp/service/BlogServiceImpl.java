package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.User;
import com.project.blogapp.mapper.blog.BlogDTOToBlogMapper;
import com.project.blogapp.mapper.blog.BlogToBlogDTOMapper;
import com.project.blogapp.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    private BlogDTOToBlogMapper blogDTOToBlogMapper;
    private BlogToBlogDTOMapper blogToBlogDTOMapper;
    private UserService userService;
    private UserRepository userRepository;

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
    public List<BlogDTO> getAllBlogPostByUsernameWithSummaries(String username, Pageable pageable) {
        log.info("{} - getAllBlogPostByUsernameWithSummaries method is working", this.getClass().getSimpleName());
        List<Blog> blogs = blogRepository.getAllByUsernamePaginated(username, pageable);
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
    public BlogDTO getBlogById(String username, Long id) {
        log.info("{} - getBlogById method is working", this.getClass().getSimpleName());
        Blog blog = blogRepository.getBlogByIdAndUsername(id, username).orElseThrow(() -> {
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

}
