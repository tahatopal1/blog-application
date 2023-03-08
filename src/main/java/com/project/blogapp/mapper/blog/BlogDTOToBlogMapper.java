package com.project.blogapp.mapper.blog;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.mapper.CustomMapper;
import org.springframework.stereotype.Component;

@Component
public class BlogDTOToBlogMapper implements CustomMapper<BlogDTO, Blog> {

    @Override
    public Blog map(BlogDTO blogDTO) {
        return Blog.builder()
                .content(blogDTO.getContent())
                .title(blogDTO.getTitle())
                .build();
    }
}
