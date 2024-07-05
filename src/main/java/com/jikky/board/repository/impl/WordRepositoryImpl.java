package com.jikky.board.repository.impl;

import com.jikky.board.mapper.WordRowMapper;
import com.jikky.board.model.Word;
import com.jikky.board.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WordRepositoryImpl implements WordRepository {

    @Autowired
    private WordRowMapper wordRowMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Word> findAllWord(String keyword) {
        String sql = "SELECT w.*, u.avatar AS creator_avatar, u.nickname AS creator_nickname " +
                "FROM words AS w " +
                "JOIN USERS AS u ON w.user_id = u.user_id " +
                "WHERE w.deleted_at IS NULL AND w.title LIKE ? " +
                "ORDER BY w.created_at DESC";

        String searchKeyword = "%" + keyword + "%";
        return jdbcTemplate.query(sql, new Object[]{searchKeyword}, wordRowMapper);
    }
}
