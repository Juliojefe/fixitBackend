package com.example.fixit.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public class CreatePostRequestImages {

    private String description;
    private Instant createdAt;
    private List<MultipartFile> images;

    public CreatePostRequestImages(String description, Instant createdAt, List<MultipartFile> images) {
        this.description = description;
        this.createdAt = createdAt;
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}