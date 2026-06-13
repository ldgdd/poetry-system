package com.poetry.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_likes")
public class UserLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "poem_id")
    private Long poemId;

    @Column(name = "user_poem_id")
    private Long userPoemId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ======== 构造方法 ========

    public UserLike() {}

    // ======== Getter / Setter ========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPoemId() { return poemId; }
    public void setPoemId(Long poemId) { this.poemId = poemId; }

    public Long getUserPoemId() { return userPoemId; }
    public void setUserPoemId(Long userPoemId) { this.userPoemId = userPoemId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
