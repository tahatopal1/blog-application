package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;

import java.util.List;

public interface TagService {

    List<BlogDTO> getAllBlogPostsByTag(Long id);

    void addTag(Long blogId, Long tagId);

    void discardTag(Long blogId, Long tagId);

}
