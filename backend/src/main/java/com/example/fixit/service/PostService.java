package com.example.fixit.service;

import com.example.fixit.dto.CreatePostRequest;
import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import com.example.fixit.model.PostImage;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.fixit.repository.PostRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
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

    public boolean likePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getLikers().contains(u) || u.getLikedPosts().contains(p)) {
                    return false;   //  already liked, no double liking
                }
                p.getLikers().add(u);
                u.getLikedPosts().add(p);
                postRepository.save(p);
                userRepository.save(u);
                return true;    //  both exist, post has not been previously liked and can now be liked
            } else {
                return false;   //  one or the other does not exist
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean savePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getSavers().contains(u) || u.getSavedPosts().contains(p)) {
                    return false;   //  already saved, no double saving
                }
                p.getSavers().add(u);
                u.getSavedPosts().add(p);
                postRepository.save(p);
                userRepository.save(u);
                return true;    //  both exist, post has not been previously saved and can now be saved
            } else {
                return false;   //  one or the other does not exist
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unlikePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getLikers().contains(u) || u.getLikedPosts().contains(p)) {
                    p.getLikers().remove(u);
                    u.getLikedPosts().remove(p);
                    postRepository.save(p);
                    userRepository.save(u);
                    return true;   //  already liked, now unlike
                }
                return false;    //  cannot unlike what has not been liked
            } else {
                return false;   //  one or the other does not exist
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unSavePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getSavers().contains(u) || u.getLikedPosts().contains(p)) {
                    p.getSavers().remove(u);
                    u.getSavedPosts().remove(p);
                    postRepository.save(p);
                    userRepository.save(u);
                    return true;   //  already saved, now unSave
                }
                return false;    //  cannot unSave what has not been saved
            } else {
                return false;   //  one or the other does not exist
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PostSummary createPost(@RequestBody CreatePostRequest request) {
        try {
            Optional<User> optUser = userRepository.findById(request.getUser_id());
            User u;
            if (optUser.isPresent()) {
                u = optUser.get();
            } else {
                throw new RuntimeException("user not found");
            }
            Post post = new Post();
            post.setDescription(request.getDescription());
            post.setUser(u);
            post.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : Instant.now());
            Set<PostImage> postImages = new HashSet<>();
            for (String imageUrl : request.getImages()) {
                PostImage postImage = new PostImage();
                postImage.setImageUrl(imageUrl);
                postImage.setPost(post);
                postImages.add(postImage);
            }
            post.setImages(postImages);
            Post savedPost = postRepository.save(post);
            return new PostSummary(savedPost);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}