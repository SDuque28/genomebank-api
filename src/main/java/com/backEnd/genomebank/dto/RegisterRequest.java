package com.backEnd.genomebank.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    // Optional list of role names, e.g. ["USER","ADMIN"]
    private List<String> roles;
}
