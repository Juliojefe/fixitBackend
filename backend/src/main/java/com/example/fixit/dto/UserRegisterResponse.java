package com.example.fixit.dto;

public class UserRegisterResponse {
    private boolean success;
    private String name;
    private String email;
    private String profilePic;
    private int userId;
    private boolean isGoogle;

    public UserRegisterResponse (boolean success, String name, String email, String profilePic, int userId, boolean isGoogle) {
        this.success = success;
        this.name = name;
        this.email = email;
        this.profilePic = profilePic;
        this.userId = userId;
        this.isGoogle = isGoogle;
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
}
