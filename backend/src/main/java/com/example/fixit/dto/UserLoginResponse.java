package com.example.fixit.dto;

public class UserLoginResponse {
    private boolean success;
    private String name;
    private String email;
    private String profilePic;
    private int userId;
    private boolean isGoogle;
    private String accessToken;
    private String refreshToken;

    public UserLoginResponse() {}

    public UserLoginResponse (boolean success, String name, String email, String profilePic, int userId, boolean isGoogle, String accessToken, String refreshToken) {
        this.success = success;
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.userId = userId;
        this.isGoogle = isGoogle;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public boolean isSuccess() {
        return success;
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

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
