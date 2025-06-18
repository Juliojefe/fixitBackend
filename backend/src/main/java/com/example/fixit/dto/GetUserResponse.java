package com.example.fixit.dto;

import com.example.fixit.model.Chat;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;

import java.util.List;

public class GetUserResponse {
    private UserRoles userRoles;
    private List<Chat> chats;
    private List<User> following;
    private List<User> followers;
    private List<Post> savedPosts;
    private List<Post> likedPosts;
    private List<Post> ownedPosts;

    public GetUserResponse(UserRoles userRoles, List<Chat> chats, List<User> following, List<User> followers, List<Post> savedPosts, List<Post> likedPosts, List<Post> ownedPosts) {
        this.userRoles = userRoles;
        this.chats = chats;
        this.following = following;
        this.followers = followers;
        this.savedPosts = savedPosts;
        this.likedPosts = likedPosts;
        this.ownedPosts = ownedPosts;
    }

    public UserRoles getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(UserRoles userRoles) {
        this.userRoles = userRoles;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public List<Post> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(List<Post> savedPosts) {
        this.savedPosts = savedPosts;
    }

    public List<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public List<Post> getOwnedPosts() {
        return ownedPosts;
    }

    public void setOwnedPosts(List<Post> ownedPosts) {
        this.ownedPosts = ownedPosts;
    }
}
