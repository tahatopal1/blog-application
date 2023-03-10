package com.project.blogapp.repository;

import com.project.blogapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // JUnit test for find user by username
    @Test
    public void givenUserObject_whenFindByUsername_thenReturnUserObject(){

        // given - precondition or setup
        User user = User.builder()
                .username("testuser")
                .password(new BCryptPasswordEncoder(10).encode("password"))
                .displayName("testuser")
                .blogs(new HashSet<>())
                .build();
        userRepository.save(user);

        // when - action or the behaviour that we are going to test
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());

        // then - verify the output
        assertThat(userOptional.isPresent()).isEqualTo(true);


    }

}
