package com.jikky.board.repository.impl;

import com.jikky.board.mapper.PostRowMapper;
import com.jikky.board.mapper.CommentRowMapper;
import com.jikky.board.model.Post;
import com.jikky.board.model.Comment;
import com.jikky.board.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRowMapper postRowMapper;

    @Autowired
    private CommentRowMapper commentRowMapper;

    @Override
    public List<Post> getAllPost(int limit, int offset) {
        String sql = "SELECT p.*, u.avatar AS creator_avatar, u.nickname AS creator_nickname " +
                "FROM posts AS p JOIN users AS u ON p.user_id = u.user_id " +
                "WHERE p.deleted_at IS NULL " +
                "ORDER BY p.created_at DESC " +
                "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{limit, offset}, postRowMapper);
    }

    @Override
    public Post getSinglePost(Long postId) {
        String sql = "SELECT p.*, u.avatar AS creator_avatar, u.nickname AS creator_nickname,u.user_id AS user_id " +
                "FROM posts AS p JOIN users AS u ON p.user_id = u.user_id " +
                "WHERE p.post_id = ? AND p.deleted_at IS NULL";
        return jdbcTemplate.queryForObject(sql, new Object[]{postId}, postRowMapper);
    }

    @Override
    public void incrementViewCount(Long postId) {
        String sql = "UPDATE posts SET count_view = count_view + 1 WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    @Override
    public Post createPost(Post post) {
        String sql = "INSERT INTO posts (title, content, post_image, user_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getPostImage(), post.getUserId());
        Long postId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        post.setPostId(postId);
        return post;
    }

    @Override
    public void updatePost(Long postId, Post post) {
        String sql = post.getPostImage() != null ?
                "UPDATE posts SET title = ?, content = ?, post_image = ? WHERE post_id = ?" :
                "UPDATE posts SET title = ?, content = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getPostImage(), postId);
    }

    @Override
    public void deletePost(Long postId) {
        String sql = "UPDATE posts SET deleted_at = CURRENT_TIMESTAMP WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        String sql = "SELECT c.*, u.avatar AS creator_avatar, u.nickname AS creator_nickname " +
                "FROM comments AS c JOIN users AS u ON c.user_id = u.user_id " +
                "WHERE c.post_id = ? AND c.deleted_at IS NULL";
        return jdbcTemplate.query(sql, new Object[]{postId}, commentRowMapper);
    }

    @Override
    public void createComment(Comment comment) {
        String sql = "INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, comment.getPostId(), comment.getUserId(), comment.getContent());
    }

    @Override
    public void updateComment(Long postId, Long commentId, String content) {
        String sql = "UPDATE comments SET content = ? WHERE comment_id = ? AND post_id = ?";
        jdbcTemplate.update(sql, content, commentId, postId);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        String sql = "UPDATE comments SET deleted_at = CURRENT_TIMESTAMP WHERE comment_id = ? AND post_id = ?";
        jdbcTemplate.update(sql, commentId, postId);
    }

    @Override
    public void incrementCommentCount(Long postId) {
        String sql = "UPDATE posts SET count_comment = count_comment + 1 WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void decrementCommentCount(Long postId) {
        String sql = "UPDATE posts SET count_comment = count_comment - 1 WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }
}
