package com.project.blogapp.service;

import com.project.blogapp.entity.User;
import com.project.blogapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();
    }

    // JUnit test for loadUserByUsername method
    @Test
    public void givenUserObject_whenLoadUserByUsername_thenUserObject(){

        // given - precondition or setup
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.of(user));

        // when - action or the behaviour that we are going to test
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // then - verify the output
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());

    }

    // JUnit test for loadUserByUsername method (negative)
    @Test
    public void givenUserObject_whenLoadUserByUsername_thenError(){

        // given - precondition or setup
        given(userRepository.findByUsername(user.getUsername()))
                .willReturn(Optional.empty());

        // when - action or the behaviour that we are going to test

        // then - verify the output
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(user.getUsername()));

    }

}
