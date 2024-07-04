package com.jikky.board.controller;

import com.jikky.board.service.impl.UserServiceImpl;
import com.jikky.board.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.jikky.board.model.User;
import com.jikky.board.service.UserService;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public ResponseEntity<?> getSingleUser(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            String email = jwtTokenUtil.getUserEmailFromToken(jwtToken);
            User singleUser = userService.getSingleUser(email);
            logger.info("User fetched: {}", singleUser);
            return ResponseEntity.ok(Map.of("status", "success", "user", singleUser));
        } catch (Exception e) {
            logger.error("Error fetching user: ", e);
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader,
                                        @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                        @RequestParam Map<String, String> userData) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        try {

                User updatedUser = userService.updateUser(userId, userData, avatar);
                return ResponseEntity.status(201).body(Map.of("message", "User Updated Successfully", "user", updatedUser));
        } catch (Exception e) {
            logger.error("Error updating user: ", e);
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteUser(@RequestParam Map<String, String> user) {
        try {
            userService.deleteUser(Long.parseLong(user.get("user_id")));
            return ResponseEntity.ok(Map.of("message", "User Deleted Successfully"));
        } catch (Exception e) {
            logger.error("Error deleting user: ", e);
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam("avatar") MultipartFile avatar, @RequestParam Map<String, Object> userData) {
        try {
            Map<String, String> stringUserData = new HashMap<>();
            userData.forEach((key, value) -> stringUserData.put(key, value.toString()));

            User newUser = userService.register(stringUserData, avatar);
            return ResponseEntity.status(201).body(Map.of("message", "User registered successfully", "user_id", newUser.getUserId()));
        } catch (Exception e) {
            logger.error("Error registering user: ", e);
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
            return ResponseEntity.ok(Map.of("message", "Logined"));
        }
        return ResponseEntity.ok(Map.of("message", "Not logged in"));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        userService.logout();
        return ResponseEntity.ok(Map.of("message", "Logout success"));
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<?> isNicknameExist(@RequestParam String nickname) {
        try {
            boolean isExist = userService.isNicknameExist(nickname);
            return ResponseEntity.ok(Map.of("message", "Success", "isExist", isExist));
        } catch (Exception e) {
            logger.error("Error checking nickname: ", e);
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @GetMapping("/email/check")
    public ResponseEntity<?> isEmailExist(@RequestParam String email) {
        try {
            boolean isExist = userService.isEmailExist(email);
            return ResponseEntity.ok(Map.of("message", "Success", "isExist", isExist));
        } catch (Exception e) {
            logger.error("Error checking email: ", e);
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }

    @PatchMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> passwordData) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        logger.info("Received user parameters: {}", userId);
        logger.info("Received passwordData parameters: {}", passwordData);

        try {
            userService.changePassword(userId, passwordData.get("password"));
            return ResponseEntity.status(201).body(Map.of("message", "Password Changed Successfully", "user_id", userId));
        } catch (Exception e) {
            logger.error("Error changing password for user_id: {}", userId, e);
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }
}
