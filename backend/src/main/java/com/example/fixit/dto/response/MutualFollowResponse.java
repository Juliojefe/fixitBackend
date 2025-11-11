package com.example.fixit.dto.response;

import com.example.fixit.model.User;

public class MutualFollowResponse extends UserNameAndPfp {
    private boolean follows;
    private boolean followsBack;

    public MutualFollowResponse() {
        this.follows = false;
        this. followsBack = false;
    }

    //  User A is the one logged in
    public MutualFollowResponse(User A, User B) {
        super(B.getName(), B.getProfilePic());
        this.follows = A.getFollowing().contains(B);
        this.followsBack = B.getFollowing().contains(A);
    }

    public boolean isFollowsBack() {
        return followsBack;
    }

    public boolean isFollows() {
        return follows;
    }
}