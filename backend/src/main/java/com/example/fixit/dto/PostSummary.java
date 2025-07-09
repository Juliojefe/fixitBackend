package com.example.fixit.dto;

import com.example.fixit.model.Post;
import com.example.fixit.model.PostImage;
import com.example.fixit.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostSummary {

    private String description;
    private String createdBy;
    private String createdByProfilePicUrl;
    private Instant createdAt;
    private Set<Integer> likeIds;
    private int likeCount;
    private Set<Integer> commentIds;
    private int commentCount;
    private List<String> imageUrls;

    public PostSummary(Post post) {
        this.description = post.getDescription();
        this.createdBy = post.getUser().getName();
        this.createdByProfilePicUrl = post.getUser().getProfilePic();
        this.createdAt = post.getCreatedAt();
        this.likeIds = getUserIds(post.getLikers());
        this.likeCount = likeIds.size();
        this.commentIds = post.getCommentIds();
        this.commentCount = post.getComments().size();
        this.imageUrls = getImageUrls(post.getPostImages());
    }

    private Set<Integer> getUserIds(Set<User> users) {
        try {
            Set<Integer> ids = new HashSet<>();
            for (User u : users) {
                ids.add(u.getUserId());
            }
            return ids;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public PostSummary() {
        this.description = "";
        this.createdBy = "";
        this.createdAt = null;
        likeIds = new HashSet<>();
        imageUrls = new ArrayList<>();
    }

    List<String> getImageUrls(Set<PostImage> postImages) {
        try {
            List<String> images = new ArrayList<>();
            for (PostImage pi : postImages) {
                images.add(pi.getImageUrl());
            }
            return images;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public Set<Integer> getLikeIds() {
        return likeIds;
    }

    public void setLikeIds(Set <Integer> likeIds) {
        this.likeIds = likeIds;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getCreatedByProfilePicUrl() {
        return createdByProfilePicUrl;
    }

    public void setCreatedByProfilePicUrl(String createdByProfilePicUrl) {
        this.createdByProfilePicUrl = createdByProfilePicUrl;
    }

    public Set<Integer> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(Set<Integer> commentIds) {
        this.commentIds = commentIds;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}