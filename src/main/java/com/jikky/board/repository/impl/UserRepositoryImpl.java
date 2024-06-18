package com.jikky.board.repository.impl;

import com.jikky.board.mapper.UserRowMapper;
import com.jikky.board.model.User;
import com.jikky.board.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRowMapper userRowMapper;

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO USERS (email, password, nickname, avatar) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getNickname(), user.getAvatar());
        String userId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", String.class);
        user.setUserId(userId);
        return user;
    }

    @Override
    public User findUserByNickname(String nickname) {
        String sql = "SELECT * FROM USERS WHERE nickname = ? AND deleted_at IS NULL";
        List<User> users = jdbcTemplate.query(sql, new Object[]{nickname}, userRowMapper);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User findUserByEmail(String email) {
        String sql = "SELECT * FROM USERS WHERE email = ? AND deleted_at IS NULL";
        List<User> users = jdbcTemplate.query(sql, new Object[]{email}, userRowMapper);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User findUserById(Long userId) {
        String sql = "SELECT * FROM USERS WHERE user_id = ? AND deleted_at IS NULL";
        List<User> users = jdbcTemplate.query(sql, new Object[]{userId}, userRowMapper);
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public void updatePassword(Long userId, String hash) {
        String sql = "UPDATE USERS SET password = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, hash, userId);
    }

    @Override
    public void updateUser(Long userId, User user) {
        String sql = "UPDATE USERS SET nickname = ?, email = ?, avatar = ?, deleted_at = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getNickname(), user.getEmail(), user.getAvatar(), user.getDeletedAt(), userId);
    }
}
