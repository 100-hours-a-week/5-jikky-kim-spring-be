package com.jikky.board.service;

import com.jikky.board.model.Post;
import com.jikky.board.model.Comment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    List<Post> getAllPost(int page, int limit);
    Post getSinglePost(Long postId);
    Post createPost(Post post, MultipartFile postImage,Long userId);
    void updatePost(Long postId, Post post, MultipartFile postImage);
    void deletePost(Long postId);
    List<Comment> getComments(Long postId);
    void createComment(Long postId, Comment comment);
    void updateComment(Long postId, Long commentId, String content);
    void deleteComment(Long postId, Long commentId);
}
