package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.mapper.blog.BlogDTOToBlogMapper;
import com.project.blogapp.mapper.blog.BlogToBlogDTOMapper;
import com.project.blogapp.repository.BlogRepository;
import com.project.blogapp.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    @Override
    public void saveBlog(BlogDTO blogDTO) {
        Blog blog = blogDTOToBlogMapper.map(blogDTO);
        blogRepository.save(blog);
    }

    @Override
    public List<BlogDTO> getAllBlogPostsWithSummaries() {
        List<Blog> blogs = blogRepository.findAll();
        return blogs.stream().map(blogToBlogDTOMapper::mapWithSummary).collect(Collectors.toList());
    }

    @Override
    public List<BlogDTO> getAllBlogPosts() {
        return blogRepository.findAll().stream().map(blogToBlogDTOMapper::map).collect(Collectors.toList());
    }

    @Override
    public void updateBlog(Long blogId, BlogDTO blogDTO) {
        Optional<Blog> optionalBlog = blogRepository.findById(blogId);
        if (optionalBlog.isEmpty()){
            throw new RuntimeException("Blog with id is not null: " + blogId);
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
            throw new RuntimeException("Tag with id is not null: " + id);
        }
        List<Blog> blogs = blogRepository.getAllBlogsByTagId(id);
        return blogs.stream().map(blogToBlogDTOMapper::map).collect(Collectors.toList());
    }

    @Override
    public void addTag(Long blogId, Long tagId) {
        blogRepository.findById(blogId).ifPresentOrElse(blog -> {
            tagRepository.findById(tagId).ifPresentOrElse(tag -> {
                blog.getTags().add(tag);
                blogRepository.save(blog);
            }, () -> {
                throw new RuntimeException("Tag with id is not null: " + blogId);
            });
        }, () -> {
            throw new RuntimeException("Blog with id is not null: " + tagId);
        });
    }

    @Override
    public void discardTag(Long blogId, Long tagId) {
        blogRepository.findById(blogId).ifPresentOrElse(blog -> {
            tagRepository.findById(tagId).ifPresentOrElse(tag -> {
                blog.getTags().remove(tag);
                blogRepository.save(blog);
            }, () -> {
                throw new RuntimeException("Tag with id is not null: " + blogId);
            });
        }, () -> {
            throw new RuntimeException("Blog with id is not null: " + tagId);
        });
    }


}
