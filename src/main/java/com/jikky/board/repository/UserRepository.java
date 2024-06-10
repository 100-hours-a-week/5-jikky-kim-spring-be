package com.jikky.board.repository;

import com.jikky.board.model.User;

public interface UserRepository {
    User createUser(User user);
    User findUserByNickname(String nickname);
    User findUserByEmail(String email);
    User findUserById(Long userId);
    void updatePassword(Long userId, String hash);
    void updateUser(Long userId, User user);
}
