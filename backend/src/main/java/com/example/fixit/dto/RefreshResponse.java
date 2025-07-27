package com.example.fixit.dto;

public class RefreshResponse {
    private String accessToken;
    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    // Getter
    public String getAccessToken() { return accessToken; }
}