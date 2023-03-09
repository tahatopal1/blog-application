package com.project.blogapp.service;

import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.entity.User;
import com.project.blogapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;

    @Override
    public void registerUser(UserDTO userDTO) {

        log.info("{} - registerUser method is working", this.getClass().getSimpleName());
        String username = userDTO.getUsername();

        if (userRepository.findByUsername(username).isPresent()){
            log.error("{} - Username is already in use: {}", this.getClass().getSimpleName(), username);
            throw new RuntimeException("Username is already in use: " + username);
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .displayName(userDTO.getDisplayName())
                .build();
        userRepository.save(user);
    }

    @Override
    public User getUserFromContextHolder() {
        String username = this.getUsernameFromContextHolder();
        return (User) userDetailsService.loadUserByUsername(username);
    }

    @Override
    public String getUsernameFromContextHolder() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
