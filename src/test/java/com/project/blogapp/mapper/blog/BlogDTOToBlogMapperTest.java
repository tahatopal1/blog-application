package com.project.blogapp.mapper.blog;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.mapper.blog.BlogDTOToBlogMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BlogDTOToBlogMapperTest {

    @InjectMocks
    private BlogDTOToBlogMapper mapper;

    // JUnit test for mapping BlogDTO object to Blog object
    @Test
    public void givenBlogDTOObject_whenMapBlogDTOToBlog_thenReturnBlogObject(){

        // given - precondition or setup
        BlogDTO blogDTO = BlogDTO.builder()
                .title("Blog Title Example")
                .content("Blog Content Example")
                .build();

        // when - action or the behaviour that we are going to test
        Blog blog = mapper.map(blogDTO);

        // then - verify the output
        assertThat(blog).isNotNull();
        assertThat(blog.getTitle()).isEqualTo(blogDTO.getTitle());
        assertThat(blog.getContent()).isEqualTo(blogDTO.getContent());

    }

}
