package com.backEnd.genomebank.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    private String password;
}
