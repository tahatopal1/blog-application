package com.project.blogapp.repository;

import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:schema-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BlogRepositoryTest {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;


    // JUnit test for
    @Test
    public void givenBlogAndTagObjects_whenGetAllBlogsByTagId_thenReturnBlogList() {

        // given - precondition or setup
        Tag tag = Tag.builder()
                .tag_name("Tag 1")
                .build();

        tagRepository.save(tag);


        IntStream.range(1, 4).forEach(
                value -> {
                    Blog blog = Blog.builder()
                            .title("Title " + value)
                            .content("Content " + value)
                            .build();
                    blogRepository.save(blog);
                }
        );

        blogRepository.findAll().forEach(
                blog -> {
                    blog.getTags().add(tag);
                    blogRepository.save(blog);
                }
        );

        // when - action or the behaviour that we are going to test
        List<Blog> blogs = blogRepository.getAllBlogsByTagId(tag.getId());

        // then - verify the output
        assertThat(blogs.size()).isEqualTo(3);

    }

}
