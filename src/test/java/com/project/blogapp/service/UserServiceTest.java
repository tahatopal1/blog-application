package com.project.blogapp.service;

import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.entity.User;
import com.project.blogapp.mapper.user.UserDTOToUserMapper;
import com.project.blogapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserDTOToUserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();
    }

    // JUnit test for registerUser method
    @Test
    public void givenUserObject_whenRegisterUser_ThenSuccessful(){

        // given - precondition or setup
        UserDTO userDTO = UserDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .displayName(user.getDisplayName())
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.empty());

        given(mapper.map(userDTO)).willReturn(user);

        // when - action or the behaviour that we are going to test
        userService.registerUser(userDTO);

        // then - verify the output
        verify(userRepository, times(1)).save(user);

    }

    // JUnit test for registerUser method (negative)
    @Test
    public void givenUserObject_whenRegisterUser_ThenError(){

        // given - precondition or setup
        UserDTO userDTO = UserDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .displayName(user.getDisplayName())
                .build();

        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));

        // when - action or the behaviour that we are going to test
        assertThrows(RuntimeException.class, () -> userService.registerUser(userDTO));

        // then - verify the output
        verify(userRepository, never()).save(user);

    }

}
