package com.erp.E02.config;

import com.erp.E02.repository.UserRepository;
import com.erp.E02.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostConstruct
    public void initAdmin() {
        // 初始化管理员账号（如果不存在）
        if (!userRepository.existsByUsername("admin")) {
            userService.registerUser("admin", "admin123", "admin@example.com");
            userService.promoteToAdmin(1L); // 假设admin的ID为1
        }
    }
}