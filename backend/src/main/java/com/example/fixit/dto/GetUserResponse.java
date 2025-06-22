package com.example.fixit.dto;

import com.example.fixit.model.Chat;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import java.util.HashSet;
import java.util.Set;

import java.util.List;

public class GetUserResponse {
    private UserRoles userRoles;
    private Set<Chat> chats;
    private Set<UserSummary> following;
    private Set<UserSummary> followers;
    private Set<Post> savedPosts;
    private Set<Post> likedPosts;
    private Set<Post> ownedPosts;

    public GetUserResponse() {}

    public GetUserResponse(UserRoles userRoles, Set<Chat> chats, Set<UserSummary> following, Set<UserSummary> followers, Set<Post> savedPosts, Set<Post> likedPosts, Set<Post> ownedPosts) {
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

    public Set<Chat> getChats() {
        return chats;
    }

    public void setChats(Set<Chat> chats) {
        this.chats = chats;
    }

    public Set<UserSummary> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserSummary> following) {
        this.following = following;
    }

    public Set<UserSummary> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserSummary> followers) {
        this.followers = followers;
    }

    public Set<Post> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(Set<Post> savedPosts) {
        this.savedPosts = savedPosts;
    }

    public Set<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Set<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Set<Post> getOwnedPosts() {
        return ownedPosts;
    }

    public void setOwnedPosts(Set<Post> ownedPosts) {
        this.ownedPosts = ownedPosts;
    }
}
