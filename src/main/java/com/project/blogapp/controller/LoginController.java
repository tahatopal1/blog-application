package com.project.blogapp.controller;

import com.project.blogapp.dto.LoginRequest;
import com.project.blogapp.dto.UserDTO;
import com.project.blogapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "Login")
@Slf4j
public class LoginController {

    private AuthenticationProvider authenticationProvider;

    private UserService userService;

    @PostMapping("/login")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity authenticateUser(){
//        Authentication authentication = authenticationProvider
//                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    @Operation(
            description = "Update a Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "202", description = "Accepted!")
            }
    )
    public ResponseEntity registerUser(@Valid @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "signupRequestAPI") @RequestBody UserDTO userDTO){
        userService.registerUser(userDTO);
        return ResponseEntity.accepted().build();
    }
    

}
