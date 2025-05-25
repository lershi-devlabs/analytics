package com.url.analytics.controller;

import com.url.analytics.dtos.LoginRequest;
import com.url.analytics.dtos.RegisterRequest;
import com.url.analytics.models.User;
import com.url.analytics.models.Role;
import com.url.analytics.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import java.security.Principal;
import com.url.analytics.dtos.UserDTO;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private UserService userservice;

    @PostMapping("/public/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        var auth = userservice.authenticateUser(loginRequest);
        ResponseCookie cookie = ResponseCookie.from("token", auth.getToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().body("Login successful");
    }

    @PostMapping("/public/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        try {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(registerRequest.getPassword());
            user.setEmail(registerRequest.getEmail());
            user.setRole(Role.ROLE_USER);
            userservice.registerUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok().body("Logged out");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        User user = userservice.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }
        return ResponseEntity.ok(new UserDTO(user));
    }
}
