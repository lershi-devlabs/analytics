package com.url.analytics.service;

import com.url.analytics.dtos.LoginRequest;
import com.url.analytics.models.User;
import com.url.analytics.repository.UserRepository;
import com.url.analytics.security.jwt.JwtAuthenticationResponse;
import com.url.analytics.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        if ((username == null || username.isEmpty()) && loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()) {
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isPresent()) {
                username = userOpt.get().getUsername();
            } else {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found with email: " + loginRequest.getEmail());
            }
        }
        if (username == null || username.isEmpty()) {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Username or email must be provided");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}
