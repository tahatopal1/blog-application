package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.entity.User;
import com.project.blogapp.mapper.blog.BlogDTOToBlogMapper;
import com.project.blogapp.mapper.blog.BlogToBlogDTOMapper;
import com.project.blogapp.repository.BlogRepository;
import com.project.blogapp.repository.TagRepository;
import com.project.blogapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    private TagRepository tagRepository;
    private BlogDTOToBlogMapper blogDTOToBlogMapper;

    private BlogToBlogDTOMapper blogToBlogDTOMapper;

    private UserService userService;

    private UserRepository userRepository;

    @Override
    public void saveBlog(BlogDTO blogDTO) {
        User user = userService.getUserFromContextHolder();
        Blog blog = blogDTOToBlogMapper.map(blogDTO);
        user.getBlogs().add(blog);
        userRepository.save(user);
    }

    @Override
    public List<BlogDTO> getAllBlogPostByUsernameWithSummaries(String username) {
        List<Blog> blogs = blogRepository.getAllByUsername(username);
        return blogs.stream().map(blogToBlogDTOMapper::mapWithSummary).collect(Collectors.toList());
    }

    @Override
    public List<BlogDTO> getAllBlogPostsByUsername(String username) {
        return blogRepository.getAllByUsername(username)
                .stream()
                .map(blogToBlogDTOMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public void updateBlog(Long blogId, BlogDTO blogDTO) {
        String username = userService.getUsernameFromContextHolder();
        Optional<Blog> optionalBlog = blogRepository.getBlogByIdAndUsername(blogId, username);
        if (optionalBlog.isEmpty()){
            throw new RuntimeException("Blog with id is null: " + blogId);
        }
        Blog blog = optionalBlog.get();
        blog.setContent(blogDTO.getContent());
        blog.setTitle(blogDTO.getTitle());
        blogRepository.save(blog);
    }

    @Override
    public List<BlogDTO> getAllBlogPostsByTag(Long id) {
        Optional<Tag> tag = tagRepository.findById(id);
        if (tag.isEmpty()){
            throw new RuntimeException("Tag with id is null: " + id);
        }
        List<Blog> blogs = blogRepository.getAllBlogsByTagId(id);
        return blogs.stream().map(blogToBlogDTOMapper::map).collect(Collectors.toList());
    }

    @Override
    public void addTag(Long blogId, Long tagId) {
        String username = userService.getUsernameFromContextHolder();
        blogRepository.getBlogByIdAndUsername(blogId, username).ifPresentOrElse(blog -> {
            tagRepository.findById(tagId).ifPresentOrElse(tag -> {
                blog.getTags().add(tag);
                blogRepository.save(blog);
            }, () -> {
                throw new RuntimeException("Tag with id not null: " + blogId);
            });
        }, () -> {
            throw new RuntimeException("Blog with is not null: " + tagId);
        });
    }

    @Override
    public void discardTag(Long blogId, Long tagId) {
        String username = userService.getUsernameFromContextHolder();
        blogRepository.getBlogByIdAndUsername(blogId, username).ifPresentOrElse(blog -> {
            tagRepository.findById(tagId).ifPresentOrElse(tag -> {
                blog.getTags().remove(tag);
                blogRepository.save(blog);
            }, () -> {
                throw new RuntimeException("Tag with id is null: " + blogId);
            });
        }, () -> {
            throw new RuntimeException("Blog with id is null: " + tagId);
        });
    }


}
