package com.backEnd.genomebank.dto.user;

import lombok.Data;

@Data
public class UserOutDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
}
