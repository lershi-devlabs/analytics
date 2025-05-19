package com.url.analytics.dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class LoginRequest {
    @Size(min = 3, max = 50)
    private String username;
    @Size(min = 8)
    private String password;
    @Email
    private String email;
}
