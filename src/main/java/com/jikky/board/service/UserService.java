package com.jikky.board.service;

import com.jikky.board.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    User register(Map<String, String> userData, MultipartFile file);
    ResponseEntity<?> login(String email, String password);
    void logout();
    boolean isNicknameExist(String nickname);
    boolean isEmailExist(String email);
    void changePassword(Long userId, String password);
    User updateUser(Long userId, Map<String, String> userData, MultipartFile file);
    void deleteUser(Long userId);
    User getSingleUser(Long userId);
}
