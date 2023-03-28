package com.project.blogapp.service;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.entity.Blog;
import com.project.blogapp.entity.Tag;
import com.project.blogapp.entity.User;
import com.project.blogapp.mapper.blog.BlogDTOToBlogMapper;
import com.project.blogapp.mapper.blog.BlogToBlogDTOMapper;
import com.project.blogapp.repository.BlogRepository;
import com.project.blogapp.repository.TagRepository;
import com.project.blogapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserService userService;

    @Mock
    private BlogToBlogDTOMapper blogToBlogDTOMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private User user;

    @BeforeEach
    void beforeAll() {
        user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();
    }

    // JUnit test for getAllBlogPostsByTag method
    @Test
    public void givenTagAndBlogObjects_whenGetAllBlogPostsByTag_thenReturnBlogList(){

        // given - precondition or setup
        List<Blog> blogs = new ArrayList<>();

        Tag tag = Tag.builder()
                .id(1L)
                .tag_name("Tag 1")
                .build();

        IntStream.range(1, 4).forEach(
                value -> {
                    Blog blog = Blog.builder()
                            .title("Title " + value)
                            .content("Content " + value)
                            .build();
                    blogs.add(blog);
                }
        );

        given(tagRepository.findById(tag.getId()))
                .willReturn(Optional.of(tag));
        given(blogRepository.getAllBlogsByTagId(tag.getId()))
                .willReturn(blogs);

        // when - action or the behaviour that we are going to test
        List<BlogDTO> blogDTOList = tagService.getAllBlogPostsByTag(tag.getId());

        // then - verify the output
        assertThat(blogDTOList).isNotNull();
        assertThat(blogDTOList.size()).isEqualTo(3);


    }

    // JUnit test for getAllBlogPostsByTag method (negative)
    @Test
    public void givenTagAndBlogObjects_whenGetAllBlogPostsByTag_thenThrowError(){

        // given - precondition or setup
        long givenId = 1L;

        given(tagRepository.findById(givenId))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> tagService.getAllBlogPostsByTag(givenId));

        // then - verify the output
        verify(blogRepository, never()).getAllBlogsByTagId(givenId);

    }

    // JUnit test for addTag method
    @Test
    public void givenTagAndBlogObjects_whenAddTag_thenSuccessfull(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .tag_name("Tag 1")
                .build();

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername()))
                .willReturn(Optional.of(blog));

        given(tagRepository.findById(tag.getId()))
                .willReturn(Optional.of(tag));

        blog.getTags().add(tag);
        given(blogRepository.save(blog)).willReturn(blog);

        // when - action or the behaviour that we are going to test
        tagService.addTag(blog.getId(), tag.getId());

        // then - verify the output
        verify(blogRepository, times(1)).save(blog);

    }

    // JUnit test for addTag method (negative - no such blog)
    @Test
    public void givenTagAndBlogObjects_whenAddTag_throwErrorsForAbsenceOfBlog(){

        // given - precondition or setup
        long blogId = 1L;
        long tagId = 1L;

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blogId, user.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> tagService.addTag(blogId, tagId));


        // then - verify the output
        verify(tagRepository, never()).findById(tagId);
        verify(blogRepository, never()).save(any(Blog.class));

    }

    // JUnit test for addTag method (negative - no such tag)
    @Test
    public void givenTagAndBlogObjects_whenAddTag_throwErrorsForAbsenceOfTag(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .build();

        long tagId = 1L;

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername()))
                .willReturn(Optional.of(blog));

        given(tagRepository.findById(tagId))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> tagService.addTag(blog.getId(), tagId));

        // then - verify the output
        verify(blogRepository, never()).save(any(Blog.class));

    }

    // JUnit test for discardTag method
    @Test
    public void givenTagAndBlogObjects_whenDiscardTag_thenSuccessfull(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .build();

        Tag tag = Tag.builder()
                .id(1L)
                .tag_name("Tag 1")
                .build();

        blog.getTags().add(tag);

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername()))
                .willReturn(Optional.of(blog));

        given(tagRepository.findById(tag.getId()))
                .willReturn(Optional.of(tag));

        blog.getTags().remove(tag);
        given(blogRepository.save(blog)).willReturn(blog);

        // when - action or the behaviour that we are going to test
        tagService.discardTag(blog.getId(), tag.getId());

        // then - verify the output
        verify(blogRepository, times(1)).save(blog);

    }

    // JUnit test for discardTag method (negative - no such blog)
    @Test
    public void givenTagAndBlogObjects_whenDiscardTag_throwErrorsForAbsenceOfBlog(){

        // given - precondition or setup
        long blogId = 1L;
        long tagId = 1L;

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blogId, user.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> tagService.discardTag(blogId, tagId));

        // then - verify the output
        verify(tagRepository, never()).findById(tagId);
        verify(blogRepository, never()).save(any(Blog.class));

    }

    // JUnit test for discardTag method (negative - no such tag)
    @Test
    public void givenTagAndBlogObjects_whenDiscardTag_throwErrorsForAbsenceOfTag(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .build();

        long tagId = 1L;

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername()))
                .willReturn(Optional.of(blog));

        given(tagRepository.findById(tagId))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> tagService.discardTag(blog.getId(), tagId));

        // then - verify the output
        verify(blogRepository, never()).save(any(Blog.class));

    }

}
