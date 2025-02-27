package com.erp.E02.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}