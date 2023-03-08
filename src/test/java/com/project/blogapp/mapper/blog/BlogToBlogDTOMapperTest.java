package com.project.blogapp.mapper.blog;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BlogToBlogDTOMapperTest {

    @InjectMocks
    private BlogToBlogDTOMapper mapper;

    private static Blog blog;

    @BeforeAll
    static void beforeAll() {
        Tag tag1 = Tag.builder()
                .tag_name("Tag 1")
                .build();

        Tag tag2 = Tag.builder()
                .tag_name("Tag 2")
                .build();

        blog = Blog.builder()
                .id(1L)
                .content("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus et elit nunc. Curabitur euismod arcu at mi eleifend cursus. Curabitur suscipit fringilla rhoncus. Nullam sed dui euismod, tempus quam in, ornare lorem. Vestibulum rutrum purus a quam tempor, ultrices placerat diam vestibulum. Morbi sed laoreet nisi. In varius erat risus, non luctus leo lacinia quis. Pellentesque faucibus, tortor a efficitur semper, nisl lacus ornare sem, a aliquam risus lacus et libero. Mauris dignissim erat vitae velit tempus finibus. Sed sagittis arcu eget est sodales, et ultricies leo dictum. Phasellus ut magna iaculis, dictum diam ut, semper est. Morbi facilisis congue eros, ut pulvinar magna cursus sed. Ut lacinia ultricies diam sed molestie.")
                .title("Blog Title")
                .tags(new HashSet<Tag>() {{
                    add(tag1);
                    add(tag2);
                }})
                .build();
    }

    // JUnit test for mapping Blog object to BlogDTO object
    @Test
    public void givenBlogObject_whenMapBlogToBlogDTO_thenReturnBlogDTOObject(){

        // when - action or the behaviour that we are going to test
        BlogDTO blogDTO = mapper.map(blog);

        // then - verify the output
        assertThat(blogDTO.getTags().size()).isEqualTo(blog.getTags().size());
        assertThat(blogDTO.getContent()).isEqualTo(blog.getContent());
        assertThat(blogDTO.getTitle()).isEqualTo(blog.getTitle());
        assertThat(blogDTO.getId()).isEqualTo(blog.getId());

    }

    // JUnit test for mapping Blog object to BlogDTO object with content summary
    @Test
    public void givenBlogObject_whenMapBlogToBlogDTO_thenReturnBlogDTOObjectWithContentSummary(){

        // when - action or the behaviour that we are going to test
        BlogDTO blogDTO = mapper.mapWithSummary(blog);

        // then - verify the output
        assertThat(blogDTO.getTags().size()).isEqualTo(blog.getTags().size());
        assertThat(blogDTO.getContent().length()).isLessThan(blog.getContent().length());
        assertThat(blogDTO.getTitle()).isEqualTo(blog.getTitle());
        assertThat(blogDTO.getId()).isEqualTo(blog.getId());

    }

}
