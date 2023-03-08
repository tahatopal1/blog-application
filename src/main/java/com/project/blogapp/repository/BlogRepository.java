package com.project.blogapp.repository;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

/*    @Query("select new com.project.blogapp.entity.Blog(b.id, b.title, concat(substring(concat(b.content,'') , 1, 100), '...'), b.tags) from Blog b")
    List<Blog> getAllBlogPostsBySummaries();*/

    @Query("select b from Blog b left join b.tags t where t.id = :id")
    List<Blog> getAllBlogsByTagId(@Param("id") Long id);

}
