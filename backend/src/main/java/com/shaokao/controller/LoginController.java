package com.shaokao.controller;

import com.shaokao.config.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class LoginController {

    // 默认管理员账号，生产环境应存数据库并加密密码
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "123456";

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Map<String, Object> result = new HashMap<>();

        if (ADMIN_USER.equals(username) && ADMIN_PASS.equals(password)) {
            String token = JwtUtil.generateToken(username);
            result.put("code", 200);
            result.put("token", token);
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
}
