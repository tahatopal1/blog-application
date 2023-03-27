package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    List<BlogDTO> search(String searchText, Pageable pageable);
}
