package com.project.blogapp.service;

import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.entity.User;
import com.project.blogapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User details not found for the user: " + username));
    }

    @Override
    public void registerUser(UserDTO userDTO) {

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()){
            throw new RuntimeException("username is already in use");
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .displayName(userDTO.getDisplayName())
                .build();
        userRepository.save(user);
    }

    @Override
    public User getUserFromContextHolder() {
        String username = this.getUsernameFromContextHolder();
        return (User) this.loadUserByUsername(username);
    }

    @Override
    public String getUsernameFromContextHolder() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
