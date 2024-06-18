package com.jikky.board.service.impl;

import com.jikky.board.service.CustomUserDetails;
import com.jikky.board.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.jikky.board.model.User;
import com.jikky.board.repository.UserRepository;
import com.jikky.board.util.JwtTokenUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

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
        String fullUrl = "/uploads/avatar/" + fileName;
        newUser.setAvatar(fullUrl);

        return userRepository.createUser(newUser);
    }

    @Override
    public ResponseEntity<?> login(String email, String password) {
        try {
            logger.log(Level.INFO, "Attempting to authenticate user: {0}", email);

            // 이메일과 비밀번호를 사용하여 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            logger.log(Level.INFO, "hi: {0}", email);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증에 성공하면 JWT 토큰 생성
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails.getUsername());

            // 응답에 JWT 토큰 포함
            Map<String, Object> response = new HashMap<>();
            response.put("message", "login success");
            response.put("user_id", userDetails.getUserId());
            response.put("token", token);

            logger.log(Level.INFO, "User {0} authenticated successfully", email);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            logger.log(Level.WARNING, "Authentication failed for user {0}: Invalid email or password", email);
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during authentication for user {0}", email);
            logger.log(Level.SEVERE, "err {0}", e);
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(Map.of("message", "Authentication failed"));
        }
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
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
                String fullUrl = "/uploads/avatar/" + fileName;
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
    public User getSingleUser(String userId) {
        return userRepository.findUserByEmail(userId);
    }
}
