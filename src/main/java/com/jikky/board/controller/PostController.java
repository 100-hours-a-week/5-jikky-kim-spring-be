package com.jikky.board.controller;

import com.jikky.board.model.Post;
import com.jikky.board.model.Comment;
import com.jikky.board.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<?> getAllPost(@RequestParam int page, @RequestParam int limit) {
        List<Post> posts = postService.getAllPost(page, limit);
        return ResponseEntity.ok().body(Map.of("message", "success", "posts", posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSinglePost(@PathVariable Long id) {
        Post post = postService.getSinglePost(id);
        return ResponseEntity.ok().body(Map.of("message", "success", "post", post));
    }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestParam Map<String, String> postData, @RequestParam MultipartFile file, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Post post = new Post();
        post.setTitle(postData.get("title"));
        post.setContent(postData.get("content"));

        Post newPost = postService.createPost(post, file, userId);
        return ResponseEntity.status(201).body(Map.of("message", "post created successfully", "newPost", newPost));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestParam Map<String, String> postData, @RequestParam(required = false) MultipartFile file) {
        Post post = new Post();
        post.setTitle(postData.get("title"));
        post.setContent(postData.get("content"));

        postService.updatePost(id, post, file);
        return ResponseEntity.ok().body(Map.of("message", "post updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().body(Map.of("message", "post deleted successfully"));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        List<Comment> comments = postService.getComments(postId);
        return ResponseEntity.ok().body(Map.of("message", "success", "comments", comments));
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> createComment(@PathVariable Long postId, @RequestBody Map<String, String> commentData, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(commentData.get("comment"));

        postService.createComment(postId, comment);
        return ResponseEntity.status(201).body(Map.of("message", "comment created successfully"));
    }

    @PatchMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody Map<String, String> commentData) {
        postService.updateComment(postId, commentId, commentData.get("comment"));
        return ResponseEntity.ok().body(Map.of("message", "comment updated successfully"));
    }

    @DeleteMapping("/{postId}/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        postService.deleteComment(postId, commentId);
        return ResponseEntity.ok().body(Map.of("message", "comment deleted successfully"));
    }
}
