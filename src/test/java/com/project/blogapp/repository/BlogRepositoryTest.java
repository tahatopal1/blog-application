package com.project.blogapp.repository;

import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BlogRepositoryTest {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();
        userRepository.save(user);

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
                    user.getBlogs().add(blog);
                    blog.setUser(user);
                    userRepository.save(user);
                }
        );

        blogRepository.findAll().forEach(
                blog -> {
                    blog.getTags().add(tag);
                    blogRepository.save(blog);
                }
        );
    }

    // JUnit test for getting all blogs by tag
    @Test
    public void givenUserBlogAndTagObjects_whenGetAllBlogsByTagId_thenReturnBlogList() {

        // given - precondition or setup
        // beforeEach method

        // when - action or the behaviour that we are going to test
        Tag tag = tagRepository.findAll().get(0);
        List<Blog> blogs = blogRepository.getAllBlogsByTagId(tag.getId());

        // then - verify the output
        assertThat(blogs.size()).isEqualTo(3);

    }

    // JUnit test for
    @Test
    public void givenUserAndBlogObjects_getAllByUsername_thenReturnBlogList(){

        // given - precondition or setup
        // beforeEach method

        // when - action or the behaviour that we are going to test
        User user = userRepository.findAll().get(0);
        List<Blog> blogs = blogRepository.getAllByUsername(user.getUsername());

        // then - verify the output
        assertThat(blogs.size()).isEqualTo(3);

    }

    // JUnit test for
    @Test
    public void givenUserAndBlogObjects_getBlogByIdAndUsername_thenReturnBlogList(){

        // given - precondition or setup
        // beforeEach method

        // when - action or the behaviour that we are going to test
        User user = userRepository.findAll().get(0);
        Blog blog = blogRepository.findAll().get(0);
        Optional<Blog> blogOptional = blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername());

        // then - verify the output
        assertThat(blogOptional.isPresent()).isEqualTo(true);

    }

}
