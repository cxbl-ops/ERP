package com.erp.E02.dto;
import com.erp.E02.model.User;
import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private String role;

    public JwtResponse(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().name();
    }
}