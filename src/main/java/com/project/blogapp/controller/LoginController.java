package com.project.blogapp.controller;

import com.project.blogapp.dto.LoginRequest;
import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LoginController {

    private AuthenticationProvider authenticationProvider;

    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity authenticateUser(){
//        Authentication authentication = authenticationProvider
//                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody UserDTO userDTO){
        userService.registerUser(userDTO);
        return ResponseEntity.accepted().build();
    }
    

}
