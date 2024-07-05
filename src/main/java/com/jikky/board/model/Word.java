package com.jikky.board.model;

import java.time.Instant;

public class Word {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private int countLike;
    private int countView;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private String creatorAvatar;
    private String creatorNickname;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getCountLike() { return countLike; }
    public void setCountLike(int countLike) { this.countLike = countLike; }
    public int getCountView() { return countView; }
    public void setCountView(int countView) { this.countView = countView; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public String getCreatorAvatar() { return creatorAvatar; }
    public void setCreatorAvatar(String creatorAvatar) { this.creatorAvatar = creatorAvatar; }
    public String getCreatorNickname() { return creatorNickname; }
    public void setCreatorNickname(String creatorNickname) { this.creatorNickname = creatorNickname; }
}
