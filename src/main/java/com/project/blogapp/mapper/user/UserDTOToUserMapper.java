package com.project.blogapp.mapper.user;

import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.entity.User;
import com.project.blogapp.mapper.CustomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDTOToUserMapper implements CustomMapper<UserDTO, User> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User map(UserDTO userDTO) {
        return User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .displayName(userDTO.getDisplayName())
                .build();
    }
}
