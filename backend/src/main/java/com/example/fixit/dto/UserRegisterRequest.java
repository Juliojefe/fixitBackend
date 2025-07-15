package com.example.fixit.dto;

public class UserRegisterRequest {
    private String name;
    private String email;
    private String password;
    private String profilePic;

    public UserRegisterRequest() {}

    public UserRegisterRequest(String name, String email, String password, String profilePic) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profilePic = profilePic;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
