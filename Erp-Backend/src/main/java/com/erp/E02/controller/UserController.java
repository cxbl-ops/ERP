package com.erp.E02.controller;

import com.erp.E02.config.StandardCreateApi;
import com.erp.E02.model.User;
import com.erp.E02.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "用户管理", description = "用户注册、登录及权限管理")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 用户注册
    @StandardCreateApi(summary = "用户注册", description = "用户注册")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String email
    ) {
        return userService.registerUser(username, password, email);
    }

    // 管理员升级用户角色
    @StandardCreateApi(summary = "管理员升级用户角色", description = "管理员升级用户角色")
    @PostMapping("/{userId}/promote-to-admin")
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可访问
    public void promoteToAdmin(@PathVariable Long userId) {
        userService.promoteToAdmin(userId);
    }
}