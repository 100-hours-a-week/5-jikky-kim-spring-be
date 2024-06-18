package com.jikky.board.controller;

import com.jikky.board.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.jikky.board.model.User;
import com.jikky.board.service.UserService;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/")
    public ResponseEntity<?> getSingleUser(@RequestHeader("Authorization") String token) {
        try {
            // JWT에서 "Bearer "를 제거하고 토큰만 추출합니다.
            String jwtToken = token.substring(7);
            // JWT에서 사용자 이름 (이메일) 을 가져옵니다.
            String email = jwtTokenUtil.getUserIdFromToken(jwtToken);

            User singleUser = userService.getSingleUser(email);
            System.out.println(singleUser);
            return ResponseEntity.ok(Map.of("status", "success", "user", singleUser));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @PatchMapping("/")
    public ResponseEntity<?> updateUser(@RequestParam Map<String, String> user, @RequestParam(value = "avatar", required = false) MultipartFile avatar, @RequestParam Map<String, String> userData) {
        try {
            User updatedUser = userService.updateUser(Long.parseLong(user.get("user_id")), userData, avatar);
            return ResponseEntity.status(201).body(Map.of("message", "User Updated Successfully", "user", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@RequestParam Map<String, String> user) {
        try {
            userService.deleteUser(Long.parseLong(user.get("user_id")));
            return ResponseEntity.ok(Map.of("message", "User Deleted Successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam("avatar") MultipartFile avatar, @RequestParam Map<String, Object> userData) {
        try {
            Map<String, String> stringUserData = new HashMap<>();
            userData.forEach((key, value) -> stringUserData.put(key, value.toString()));

            User newUser = userService.register(stringUserData, avatar);
            return ResponseEntity.status(201).body(Map.of("message", "user registered successfully", "user_id", newUser.getUserId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        if (email == null || password == null) {
            return ResponseEntity.status(400).body(Map.of("message", "Please provide your email and password"));
        }
        return userService.login(email, password);
    }

    @GetMapping("/login/auto")
    public ResponseEntity<?> autoLogin(@RequestParam Map<String, String> user) {
        if (user != null) {
            return ResponseEntity.ok(Map.of("message", "logined"));
        }
        return ResponseEntity.ok(Map.of("message", "not logged in"));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        userService.logout();
        return ResponseEntity.ok(Map.of("message", "logout success"));
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<?> isNicknameExist(@RequestParam String nickname) {
        try {
            boolean isExist = userService.isNicknameExist(nickname);
            return ResponseEntity.ok(Map.of("message", "success", "isExist", isExist));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @GetMapping("/email/check")
    public ResponseEntity<?> isEmailExist(@RequestParam String email) {
        try {
            boolean isExist = userService.isEmailExist(email);
            return ResponseEntity.ok(Map.of("message", "success", "isExist", isExist));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @PatchMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestParam Map<String, String> user, @RequestBody Map<String, String> passwordData) {
        try {
            userService.changePassword(Long.parseLong(user.get("user_id")), passwordData.get("password"));
            return ResponseEntity.status(201).body(Map.of("message", "Password Changed Successfully", "user_id", user.get("user_id")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }
}
