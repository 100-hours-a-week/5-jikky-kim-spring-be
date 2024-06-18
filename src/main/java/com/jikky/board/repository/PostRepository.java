package com.jikky.board.repository;

import com.jikky.board.model.Post;
import com.jikky.board.model.Comment;

import java.util.List;

public interface PostRepository {
    List<Post> getAllPost(int limit, int offset);
    Post getSinglePost(Long postId);
    void incrementViewCount(Long postId);
    Post createPost(Post post);
    void updatePost(Long postId, Post post);
    void deletePost(Long postId);
    List<Comment> getCommentsByPostId(Long postId);
    void createComment(Comment comment);
    void updateComment(Long postId, Long commentId, String content);
    void deleteComment(Long postId, Long commentId);
    void incrementCommentCount(Long postId);
    void decrementCommentCount(Long postId);
}
