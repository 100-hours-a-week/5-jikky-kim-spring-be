package com.jikky.board.mapper;

import com.jikky.board.model.Word;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WordRowMapper implements RowMapper<Word> {

    @Override
    public Word mapRow(ResultSet rs, int rowNum) throws SQLException {
        Word word = new Word();
        word.setId(rs.getLong("word_id"));
        word.setUserId(rs.getLong("user_id"));
        word.setTitle(rs.getString("title"));
        word.setContent(rs.getString("content"));
        word.setCountLike(rs.getInt("count_like"));
        word.setCountView(rs.getInt("count_view"));
        word.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        word.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
        word.setCreatorAvatar(rs.getString("creator_avatar"));
        word.setCreatorNickname(rs.getString("creator_nickname"));
        return word;
    }
}
