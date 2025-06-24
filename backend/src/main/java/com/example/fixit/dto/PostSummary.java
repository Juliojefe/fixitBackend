package com.example.fixit.dto;

import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class PostSummary {

    private String description;
    private String createdBy;
    private Instant createdAt;
    private Set<UserSummary> likers;
    private int likeCount;

    public PostSummary(Post post) {
        this.description = post.getDescription();
        this.createdBy = post.getUser().getName();
        this.createdAt = post.getCreatedAt();
        this.likers = summarizeUsers(post.getLikers());
        this.likeCount = likers.size();
    }

    private Set<UserSummary> summarizeUsers(Set<User> users) {
        try {
            Set<UserSummary> summary = new HashSet<>();
            for (User u : users) {
                summary.add(new UserSummary(u));
            }
            return summary;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<UserSummary> getLikers() {
        return likers;
    }

    public void setLikers(Set<UserSummary> likers) {
        this.likers = likers;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}