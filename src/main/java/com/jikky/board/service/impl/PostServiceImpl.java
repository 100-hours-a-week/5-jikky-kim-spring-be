package com.jikky.board.service.impl;

import com.jikky.board.model.Post;
import com.jikky.board.model.Comment;
import com.jikky.board.repository.PostRepository;
import com.jikky.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    private static final String IMAGE_PATH = "uploads/post";

    @Override
    public List<Post> getAllPost(int page, int limit) {
        int offset = (page - 1) * limit;
        return postRepository.getAllPost(limit, offset);
    }

    @Override
    public Post getSinglePost(Long postId) {
        postRepository.incrementViewCount(postId);
        return postRepository.getSinglePost(postId);
    }

    @Override
    public Post createPost(Post post, MultipartFile file, Long userId) {
        String fileName = file.getOriginalFilename().split("\\.")[0] + "_" + System.currentTimeMillis() + "." + file.getOriginalFilename().split("\\.")[1];
        Path filePath = Paths.get(System.getProperty("user.dir"), IMAGE_PATH, fileName);

        try {
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        post.setPostImage("/" + IMAGE_PATH + "/" + fileName);
        post.setUserId(userId);
        return postRepository.createPost(post);
    }

    @Override
    public void updatePost(Long postId, Post post, MultipartFile file) {
        if (file != null) {
            String fileName = file.getOriginalFilename().split("\\.")[0] + "_" + System.currentTimeMillis() + "." + file.getOriginalFilename().split("\\.")[1];
            Path filePath = Paths.get(System.getProperty("user.dir"), IMAGE_PATH, fileName);

            try {
                Files.createDirectories(filePath.getParent());
                file.transferTo(filePath.toFile());
                post.setPostImage("/" + IMAGE_PATH + "/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
            }
        }

        postRepository.updatePost(postId, post);
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deletePost(postId);
    }

    @Override
    public List<Comment> getComments(Long postId) {
        return postRepository.getCommentsByPostId(postId);
    }

    @Override
    public void createComment(Long postId, Comment comment) {
        postRepository.createComment(comment);
        postRepository.incrementCommentCount(postId);
    }

    @Override
    public void updateComment(Long postId, Long commentId, String content) {
        postRepository.updateComment(postId, commentId, content);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        postRepository.deleteComment(postId, commentId);
        postRepository.decrementCommentCount(postId);
    }
}
