package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.mapper.blog.BlogToBlogDTOMapper;
import com.project.blogapp.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class TagServiceImpl implements TagService {

    private BlogRepository blogRepository;

    private TagRepository tagRepository;
    private BlogToBlogDTOMapper blogToBlogDTOMapper;
    private UserService userService;

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

}
