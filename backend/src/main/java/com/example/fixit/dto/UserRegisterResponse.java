package com.example.fixit.dto;

public class UserRegisterResponse {
    private String name;
    private String email;
    private String profilePic;
    private boolean isGoogle;
    private String accessToken;
    private String refreshToken;

    public UserRegisterResponse(String name, String email, String profilePic, boolean isGoogle, String accessToken, String refreshToken) {
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.isGoogle = isGoogle;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public boolean isGoogle() {
        return isGoogle;
    }

    public void setGoogle(boolean google) {
        isGoogle = google;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
