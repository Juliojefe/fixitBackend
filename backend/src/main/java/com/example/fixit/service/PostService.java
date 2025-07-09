package com.example.fixit.service;

import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.fixit.repository.PostRepository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public PostSummary getPostSummaryById(int postId) {
        try {
            Optional<Post> OptPost = postRepository.findById(postId);
            if (OptPost.isPresent()) {
                Post p = OptPost.get();
                return new PostSummary(p);
            } else {
                return new PostSummary();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<PostSummary> getAllPosts() {
        try {
            Set<PostSummary> ps = new HashSet<>();
            List<Post> allPosts = postRepository.findAll();
            for (Post p : allPosts) {
                ps.add(new PostSummary(p));
            }
            return ps;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Integer> getAllPostIds() {
        try {
            List<Post> allPosts = postRepository.findAll();
            Set<Integer> ids = new HashSet<>();
            for (Post p : allPosts) {
                ids.add(p.getPostId());
            }
            return ids;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Integer> getOwnedPostByUserId(int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User u = optUser.get();
                Set<Post> ownedPosts = u.getOwnedPosts();
                Set<Integer> ids = new HashSet<>();
                for (Post p : ownedPosts) {
                    ids.add(p.getPostId());
                }
                return ids;
            } else {
                return new HashSet<>();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Integer> getLikedPostByUserId(int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User u = optUser.get();
                Set<Post> ownedPosts = u.getLikedPosts();
                Set<Integer> ids = new HashSet<>();
                for (Post p : ownedPosts) {
                    ids.add(p.getPostId());
                }
                return ids;
            } else {
                return new HashSet<>();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Integer> getSavedPostsByUserId(int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User u = optUser.get();
                Set<Post> ownedPosts = u.getSavedPosts();
                Set<Integer> ids = new HashSet<>();
                for (Post p : ownedPosts) {
                    ids.add(p.getPostId());
                }
                return ids;
            } else {
                return new HashSet<>();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}