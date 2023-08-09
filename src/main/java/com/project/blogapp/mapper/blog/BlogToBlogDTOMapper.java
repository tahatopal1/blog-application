package com.project.blogapp.mapper.blog;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.mapper.CustomMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlogToBlogDTOMapper implements CustomMapper<Blog, BlogDTO> {
    @Override
    public BlogDTO map(Blog blog) {
        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .tags(populateTags(blog))
                .build();
    }


    public BlogDTO mapWithSummary(Blog blog){
        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent().substring(0, 100).concat("..."))
                .tags(populateTags(blog))
                .build();
    }

    private Set<String> populateTags(Blog blog) {
        Set<Tag> tags = blog.getTags();
        if (!CollectionUtils.isEmpty(tags))
            return tags.stream().map(Tag::getTag_name).collect(Collectors.toSet());
        return new HashSet<String>();
    }
}
