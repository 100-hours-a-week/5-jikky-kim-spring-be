package com.jikky.board.service.impl;

import com.jikky.board.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.jikky.board.model.User;
import com.jikky.board.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    private static final String UPLOAD_DIR = "uploads/avatar"; // 파일 저장 경로 설정

    @Override
    public User register(Map<String, String> userData, MultipartFile file) {
        // 파일 이름과 경로 설정
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = file.getOriginalFilename().split("\\.")[0] + "_" + System.currentTimeMillis() + fileExtension;
        Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
        Path filePath = uploadPath.resolve(fileName);

        // 파일 저장
        try {
            Files.createDirectories(uploadPath); // 디렉토리가 없으면 생성
            file.transferTo(filePath.toFile()); // 파일 저장
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        // 사용자 정보 설정
        String hash = passwordEncoder.encode(userData.get("password"));
        User newUser = new User();
        newUser.setEmail(userData.get("email"));
        newUser.setPassword(hash);
        newUser.setNickname(userData.get("nickname"));
        String relativePath = "/uploads/avatar/" + fileName;
        String fullUrl = "http://" + serverAddress + ":" + serverPort + relativePath;
        newUser.setAvatar(fullUrl);

        return userRepository.createUser(newUser);
    }

    @Override
    public ResponseEntity<?> login(String email, String password) {
        User user = userRepository.findUserByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }
        return ResponseEntity.ok(Map.of("message", "login success", "user_id", user.getUserId()));
    }

    @Override
    public void logout() {
        // Logout logic here (e.g., invalidate session)
    }

    @Override
    public boolean isNicknameExist(String nickname) {
        return userRepository.findUserByNickname(nickname) != null;
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findUserByEmail(email) != null;
    }

    @Override
    public void changePassword(Long userId, String password) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Same As The Original Password");
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.updatePassword(userId, user.getPassword());
    }

    @Override
    public User updateUser(Long userId, Map<String, String> userData, MultipartFile file) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (file != null) {
            String fileName = file.getOriginalFilename().split("\\.")[0] + "_" + System.currentTimeMillis() + "." + file.getOriginalFilename().split("\\.")[1];
            Path uploadPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
            Path filePath = uploadPath.resolve(fileName);
            try {
                Files.createDirectories(uploadPath);
                file.transferTo(filePath.toFile());
                String relativePath = "/uploads/avatar/" + fileName;
                String fullUrl = "http://" + serverAddress + ":" + serverPort + relativePath;
                user.setAvatar(fullUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }
        user.setNickname(userData.get("nickname"));
        user.setEmail(userData.get("email"));
        userRepository.updateUser(userId, user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.updateUser(userId, user);
    }

    @Override
    public User getSingleUser(Long userId) {
        return userRepository.findUserById(userId);
    }
}
