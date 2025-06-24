package com.example.fixit.dto;

import com.example.fixit.model.Chat;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import java.util.HashSet;
import java.util.Set;

public class GetUserResponse {
    private boolean isMechanic;
    private boolean isAdmin;
    private Set<Integer> chatIds;
    private Set<Integer> followingIds;
    private Set<Integer> followersIds;
    private Set<Integer> savedPostsIds;
    private Set<Integer> likedPostsIds;
    private Set<Integer> ownedPostsIds;
    private int followerCount;
    private int followingCount;

    public GetUserResponse() {
        this.isMechanic = false;
        this.isAdmin = false;
        this.chatIds = new HashSet<>();
        this.followingIds = new HashSet<>();
        this.followersIds = new HashSet<>();
        this.savedPostsIds = new HashSet<>();
        this.likedPostsIds = new HashSet<>();
        this.ownedPostsIds = new HashSet<>();
        this.followerCount = 0;
        this.followingCount = 0;
    }

    public GetUserResponse(User u) {
        this.isMechanic = u.getUserRoles().getIsMechanic();
        this.isAdmin = u.getUserRoles().getIsAdmin();
        this.chatIds = getChatIds(u.getChats());
        this.followingIds = getUserIds(u.getFollowing());
        this.followersIds = getUserIds(u.getFollowers());
        this.savedPostsIds = getPostIds(u.getSavedPosts());
        this.likedPostsIds = getPostIds(u.getLikedPosts());
        this.ownedPostsIds = getPostIds(u.getOwnedPosts());
        this.followerCount = followersIds.size();
        this.followingCount = followingIds.size();
    }

    private Set<Integer> getChatIds(Set<Chat> chats) {
        try {
            Set<Integer> ids = new HashSet<>();
            for (Chat cht : chats) {
                ids.add(cht.getChatId());
            }
            return ids;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Integer> getUserIds(Set<User> users) {
        try {
            Set<Integer> ids = new HashSet<>();
            for (User u : users) {
                ids.add(u.getUserId());
            }
            return ids;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Integer> getPostIds(Set<Post> posts) {
        try {
            Set<Integer> ids = new HashSet<>();
            for (Post p : posts) {
                ids.add(p.getPostId());
            }
            return ids;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<ChatSummary> summarizeChat(Set<Chat> chats) {
        try {
            Set<ChatSummary> chatSum = new HashSet<>();
            for (Chat cht : chats) {
                chatSum.add(new ChatSummary(cht));
            }
            return chatSum;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<UserSummary> summarizeUsers(Set<User> users) {
        try {
            Set<UserSummary> summary = new HashSet<>();
            for (User u : users) {
                summary.add(new UserSummary(u));
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

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
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

    public Set<Integer> getChatIds() {
        return chatIds;
    }

    public void setChatIds(Set<Integer> chatIds) {
        this.chatIds = chatIds;
    }

    public Set<Integer> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(Set<Integer> followingIds) {
        this.followingIds = followingIds;
    }

    public Set<Integer> getFollowersIds() {
        return followersIds;
    }

    public void setFollowersIds(Set<Integer> followersIds) {
        this.followersIds = followersIds;
    }

    public Set<Integer> getSavedPostsIds() {
        return savedPostsIds;
    }

    public void setSavedPostsIds(Set<Integer> savedPostsIds) {
        this.savedPostsIds = savedPostsIds;
    }

    public Set<Integer> getLikedPostsIds() {
        return likedPostsIds;
    }

    public void setLikedPostsIds(Set<Integer> likedPostsIds) {
        this.likedPostsIds = likedPostsIds;
    }

    public Set<Integer> getOwnedPostsIds() {
        return ownedPostsIds;
    }

    public void setOwnedPostsIds(Set<Integer> ownedPostsIds) {
        this.ownedPostsIds = ownedPostsIds;
    }
}
