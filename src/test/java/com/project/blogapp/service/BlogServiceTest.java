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
import org.junit.jupiter.api.BeforeAll;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BlogDTOToBlogMapper blogDTOToBlogMapper;

    @Mock
    private BlogToBlogDTOMapper blogToBlogDTOMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BlogServiceImpl blogService;

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

    // JUnit test for saveBlog method
    @Test
    public void givenBlogDTOObject_whenSaveBlog_thenSuccess(){

        // given - precondition or setup
        BlogDTO blogDTO = BlogDTO.builder()
                .title("Blog Title")
                .content("Blog Content")
                .build();

        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .build();

        given(userService.getUserFromContextHolder()).willReturn(user);
        given(blogDTOToBlogMapper.map(blogDTO)).willReturn(blog);

        // when - action or the behaviour that we are going to test
        blogService.saveBlog(blogDTO);

        // then - verify the output
        verify(userRepository, times(1)).save(user);

    }

    // JUnit test for getAllByUsername
    @Test
    public void givenBlogList_whenGetAllBlogPosts_thenReturnBlogList(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .build();

        Blog blog2 = Blog.builder()
                .title("Blog Title 2")
                .content("Blog Content 2")
                .build();

        given(blogRepository.getAllByUsername(user.getUsername())).willReturn(List.of(blog, blog2));

        // when - action or the behaviour that we are going to test
        List<BlogDTO> blogList = blogService.getAllBlogPostsByUsername(user.getUsername());

        // then - verify the output
        assertThat(blogList).isNotNull();
        assertThat(blogList.size()).isEqualTo(2);

    }

    // JUnit test for getAllBlogPostsWithSummaries
    @Test
    public void givenBlogList_whenGetAllBlogPosts_thenReturnBlogListWithSummaries(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .title("Blog Title")
                .content("Blog Content")
                .build();

        Blog blog2 = Blog.builder()
                .title("Blog Title 2")
                .content("Blog Content 2")
                .build();

        given(blogRepository.getAllByUsername(user.getUsername())).willReturn(List.of(blog, blog2));

        // when - action or the behaviour that we are going to test
        List<BlogDTO> blogList = blogService.getAllBlogPostByUsernameWithSummaries(user.getUsername());

        // then - verify the output
        assertThat(blogList).isNotNull();
        assertThat(blogList.size()).isEqualTo(2);

    }

    // JUnit test for updateBlog method
    @Test
    public void givenBlogObject_whenUpdateBlog_thenSuccess(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .build();

        BlogDTO blogDTO = BlogDTO.builder()
                .title("Blog Title Updated")
                .content("Blog Content Updated")
                .build();

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername()))
                .willReturn(Optional.of(blog));

        // when - action or the behaviour that we are going to test
        blogService.updateBlog(blog.getId(), blogDTO);

        // then - verify the output
        verify(blogRepository, times(1)).save(blog);

    }

    // JUnit test for updateBlog method (negative)
    @Test
    public void givenBlogObject_whenUpdateBlog_thenThrowError(){

        long givenId = 1L;

        BlogDTO blogDTO = BlogDTO.builder()
                .title("Blog Title Updated")
                .content("Blog Content Updated")
                .build();

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(givenId, user.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> blogService.updateBlog(givenId, blogDTO));

        // then - verify the output
        verify(blogRepository, never()).save(any(Blog.class));

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
        List<BlogDTO> blogDTOList = blogService.getAllBlogPostsByTag(tag.getId());

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
        assertThrows(RuntimeException.class, () -> blogService.getAllBlogPostsByTag(givenId));

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
        blogService.addTag(blog.getId(), tag.getId());

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
        assertThrows(RuntimeException.class, () -> blogService.addTag(blogId, tagId));


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
        assertThrows(RuntimeException.class, () -> blogService.addTag(blog.getId(), tagId));

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
        blogService.discardTag(blog.getId(), tag.getId());

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
        assertThrows(RuntimeException.class, () -> blogService.discardTag(blogId, tagId));

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
        assertThrows(RuntimeException.class, () -> blogService.discardTag(blog.getId(), tagId));

        // then - verify the output
        verify(blogRepository, never()).save(any(Blog.class));

    }




}
