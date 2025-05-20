package com.example.fixit.model;

import jakarta.persistence.*;
import java.security.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "description", nullable = false, length = 3000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @ManyToMany(mappedBy = "savedPosts")
    private Set<User> savers = new HashSet<>();

    @ManyToMany(mappedBy = "likedPosts")
    private Set<User> likers = new HashSet<>();

    // Getters and setters
    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Set<User> getSavers() {
        return savers;
    }

    public void setSavers(Set<User> savers) {
        this.savers = savers;
    }

    public Set<User> getLikers() {
        return likers;
    }

    public void setLikers(Set<User> likers) {
        this.likers = likers;
    }
}