package com.example.fixit.dto;

import com.example.fixit.model.User;

public class UserSummary {
    private String name;
    private String profilePic;

    public UserSummary(String name, String profilePic) {
        this.name = name;
        this.profilePic = profilePic;
    }

    public UserSummary(User u) {
        this.name = u.getName();
        this.profilePic = u.getProfilePic();
    }

    public UserSummary() {
        this.name = "";
        this.profilePic = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
