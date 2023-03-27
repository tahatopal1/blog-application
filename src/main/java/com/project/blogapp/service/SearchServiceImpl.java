package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.mapper.blog.BlogToBlogDTOMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private BlogToBlogDTOMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<BlogDTO> search(String searchText, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        SearchResult<Blog> result = searchSession.search(Blog.class).extension(ElasticsearchExtension.get())
                .where(f -> f.simpleQueryString().fields("title", "tags.tag_name").matching(searchText))
                .fetch(pageNumber * pageSize, pageSize);

        List<Blog> results = result.hits();
        return results.stream()
                .map(mapper::mapWithSummary)
                .collect(Collectors.toList());
    }
}
