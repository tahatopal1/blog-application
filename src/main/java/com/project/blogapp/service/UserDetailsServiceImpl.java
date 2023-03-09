package com.project.blogapp.service;

import com.project.blogapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("{} - loadUserByUsername method is working", this.getClass().getSimpleName());
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("{} - User details not found for the user: {}", this.getClass().getSimpleName(), username);
                    throw new UsernameNotFoundException("User details not found for the user: " + username);
                });
    }
}
