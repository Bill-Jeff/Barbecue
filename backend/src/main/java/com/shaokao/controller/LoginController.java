package com.shaokao.controller;

import com.shaokao.config.JwtUtil;
import com.shaokao.entity.User;
import com.shaokao.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Map<String, Object> result = new HashMap<>();

        User user = userService.findByUsername(username);
        if (user != null && user.getStatus() == 1 && userService.checkPassword(password, user.getPassword())) {
            String token = JwtUtil.generateToken(username);
            result.put("code", 200);
            result.put("token", token);
            result.put("nickname", user.getNickname());
            result.put("role", user.getRole());
            result.put("message", "登录成功");
        } else {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
        }

        return result;
    }

    @GetMapping("/check")
    public Map<String, Object> checkAuth() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "已登录");
        return result;
    }

    // ========== 用户管理 ==========

    @GetMapping("/users")
    public java.util.List<User> listUsers() {
        return userService.listAll();
    }

    @PostMapping("/users")
    public Map<String, Object> saveUser(@RequestBody User user) {
        userService.save(user);
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("message", "保存成功");
        return result;
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }
}
