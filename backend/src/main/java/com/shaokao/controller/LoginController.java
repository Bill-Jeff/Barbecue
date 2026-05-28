package com.shaokao.controller;

import com.shaokao.config.BusinessException;
import com.shaokao.config.JwtUtil;
import com.shaokao.config.Result;
import com.shaokao.config.ResultCode;
import com.shaokao.entity.User;
import com.shaokao.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        User user = userService.findByUsername(username);
        if (user != null && user.getStatus() == 1 && userService.checkPassword(password, user.getPassword())) {
            String token = JwtUtil.generateToken(username);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("nickname", user.getNickname());
            data.put("role", user.getRole());
            return Result.success(data);
        }
        throw new BusinessException(ResultCode.LOGIN_FAILED);
    }

    @GetMapping("/check")
    public Result<Void> checkAuth() {
        return Result.success();
    }

    // ========== 用户管理 ==========

    @GetMapping("/users")
    public Result<List<User>> listUsers() {
        return Result.success(userService.listAll());
    }

    @PostMapping("/users")
    public Result<Void> saveUser(@RequestBody User user) {
        userService.save(user);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
