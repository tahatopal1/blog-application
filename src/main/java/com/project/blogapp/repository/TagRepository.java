package com.project.blogapp.repository;

import com.project.blogapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
/*
    @Query("select t from Tag  t where t.id in :idS")
    List<Tag> getListByIds(@Param("idS") List<Long> idS);
*/
}
