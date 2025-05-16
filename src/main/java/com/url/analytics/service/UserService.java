package com.url.analytics.service;

import com.url.analytics.models.User;
import com.url.analytics.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
