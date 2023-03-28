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
import org.springframework.data.domain.PageRequest;
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

        PageRequest pageRequest = PageRequest.of(0, 2);

        given(blogRepository.getAllByUsernamePaginated(user.getUsername(), pageRequest)).willReturn(List.of(blog, blog2));

        // when - action or the behaviour that we are going to test
        List<BlogDTO> blogList = blogService.getAllBlogPostByUsernameWithSummaries(user.getUsername(), pageRequest);

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

    // JUnit test for getBlogById
    @Test
    public void givenBlogObject_whenGetBlogById_thenReturnBlog(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();

        BlogDTO blogDTO = BlogDTO.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .build();

        given(blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername()))
                .willReturn(Optional.of(blog));

        given(blogToBlogDTOMapper.map(blog))
                .willReturn(blogDTO);

        // when - action or the behaviour that we are going to test
        BlogDTO blogById = blogService.getBlogById(user.getUsername(), 1L);

        // then - verify the output
        assertThat(blogById.getId()).isEqualTo(blog.getId());
        assertThat(blogById.getTitle()).isEqualTo(blog.getTitle());
        assertThat(blogById.getContent()).isEqualTo(blog.getContent());

    }

    // JUnit test for getBlogById (negative)
    @Test
    public void givenBlogObject_whenGetBlogById_thenError(){

        // given - precondition or setup
        long id = 1L;

        given(blogRepository.getBlogByIdAndUsername(id, user.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> blogService.getBlogById(user.getUsername(), id));

        // then - verify the output
        verify(blogToBlogDTOMapper, never()).map(any(Blog.class));

    }

    // JUnit test for deleteBlogById
    @Test
    public void givenBlogObject_whenDeleteBlogById_thenSuccessful(){

        // given - precondition or setup
        Blog blog = Blog.builder()
                .id(1L)
                .title("Blog Title")
                .content("Blog Content")
                .user(user)
                .build();

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blog.getId(), user.getUsername()))
                .willReturn(Optional.of(blog));

        // when - action or the behaviour that we are going to test
        blogService.deleteBlogById(blog.getId());

        // then - verify the output
        verify(blogRepository, times(1)).deleteById(blog.getId());

    }

    // JUnit test for deleteBlogById (negative)
    @Test
    public void givenBlogObject_whenDeleteBlogById_thenError(){

        // given - precondition or setup
        long blogId = 1L;

        given(userService.getUsernameFromContextHolder())
                .willReturn(user.getUsername());

        given(blogRepository.getBlogByIdAndUsername(blogId, user.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> blogService.deleteBlogById(blogId));

        // then - verify the output
        verify(blogRepository, never()).deleteById(blogId);

    }

}
