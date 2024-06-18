package com.jikky.board.mapper;

import com.jikky.board.model.Post;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PostRowMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getLong("post_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setPostImage(rs.getString("post_image"));
        post.setCreatorAvatar(rs.getString("creator_avatar"));
        post.setUserId(rs.getLong("user_id"));
        post.setCreatorNickname(rs.getString("creator_nickname"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        post.setUpdatedAt(rs.getTimestamp("updated_at"));
        post.setDeletedAt(rs.getTimestamp("deleted_at"));
        post.setCountView(rs.getInt("count_view"));
        post.setCountComment(rs.getInt("count_comment"));
        return post;
    }
}
