package com.project.blogapp.mapper.user;

import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserDTOToUserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDTOToUserMapper mapper;

    // JUnit test for mapping UserDTO object to User object
    @Test
    public void givenUserDTOObject_whenMapUserDTOToUser_thenReturnUserObject(){

        // given - precondition or setup
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .password("password")
                .displayName("testuser")
                .build();

        given(passwordEncoder.encode(userDTO.getPassword()))
                .willReturn("encodedpassword");

        // when - action or the behaviour that we are going to test
        User user = mapper.map(userDTO);

        // then - verify the output
        assertThat(userDTO.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDTO.getPassword()).isNotEqualTo(user.getPassword());
        assertThat(userDTO.getDisplayName()).isEqualTo(user.getDisplayName());


    }

}
