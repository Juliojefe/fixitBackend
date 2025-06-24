package com.example.fixit.dto;

import com.example.fixit.model.Chat;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import com.example.fixit.model.UserRoles;
import java.util.HashSet;
import java.util.Set;

import java.util.List;

public class GetUserResponse {
    private boolean isMechanic;
    private boolean isAdmin;
    private Set<ChatSummary> chats;
    private Set<UserSummary> following;
    private Set<UserSummary> followers;
    private Set<PostSummary> savedPosts;
    private Set<PostSummary> likedPosts;
    private Set<PostSummary> ownedPosts;

    public GetUserResponse() {}
    

    public GetUserResponse(User u) {
        this.isMechanic = u.getUserRoles().getIsMechanic();
        this.isAdmin = u.getUserRoles().getIsAdmin();
        this.chats = summarizeChat(u.getChats());
        this.following = summarizeUsers(u.getFollowing());
        this.followers = summarizeUsers(u.getFollowers());
        this.savedPosts = summarizePosts(u.getSavedPosts());
        this.likedPosts = summarizePosts(u.getLikedPosts());
        this.ownedPosts = summarizePosts(u.getOwnedPosts());
    }

    private Set<ChatSummary> summarizeChat(Set<Chat> chats) {
        try {
            Set<ChatSummary> chatSum = new HashSet<>();
            for (Chat cht : chats) {
                chatSum.add(new ChatSummary(cht));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<UserSummary> summarizeUsers(Set<User> users) {
        try {
            Set<UserSummary> summary = new HashSet<>();
            for (User u : users) {
                summary.add(new UserSummary(u.getName(), u.getProfilePic()));
            }
            return summary;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<PostSummary> summarizePosts(Set<Post> posts) {
        try {
            Set<PostSummary> pSum = new HashSet<>();
            for (Post p : posts) {
                pSum.add(new PostSummary(p));
            }
            return pSum;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isMechanic() {
        return isMechanic;
    }

    public void setMechanic(boolean mechanic) {
        isMechanic = mechanic;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Set<ChatSummary> getChats() {
        return chats;
    }

    public void setChats(Set<ChatSummary> chats) {
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

    public Set<PostSummary> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(Set<PostSummary> savedPosts) {
        this.savedPosts = savedPosts;
    }

    public Set<PostSummary> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Set<PostSummary> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Set<PostSummary> getOwnedPosts() {
        return ownedPosts;
    }

    public void setOwnedPosts(Set<PostSummary> ownedPosts) {
        this.ownedPosts = ownedPosts;
    }
}
