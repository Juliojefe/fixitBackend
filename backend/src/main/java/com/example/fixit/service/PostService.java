package com.example.fixit.service;

import com.example.fixit.dto.CreatePostRequestImages;
import com.example.fixit.dto.CreatePostRequestUrl;
import com.example.fixit.dto.PostSummary;
import com.example.fixit.model.Post;
import com.example.fixit.model.PostImage;
import com.example.fixit.model.User;
import com.example.fixit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.fixit.repository.PostRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Transactional(readOnly = true)
    public ResponseEntity<PostSummary> getPostSummaryById(int postId) {
        try {
            Optional<Post> OptPost = postRepository.findById(postId);
            if (OptPost.isPresent()) {
                Post p = OptPost.get();
                return ResponseEntity.ok(new PostSummary(p));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Set<Integer>> getAllPostIds() {
        try {
            List<Post> allPosts = postRepository.findAll();
            Set<Integer> ids = new HashSet<>();
            for (Post p : allPosts) {
                ids.add(p.getPostId());
            }
            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<List<Integer>> getFollowingPostIds(User u) {
        try {
            Set<User> followedUsers = u.getFollowing();
            List<Post> followingPosts = new ArrayList<>();
            for (User followed : followedUsers) {
                followingPosts.addAll(followed.getOwnedPosts());
            }
            List<Integer> ids = followingPosts.stream()
                    .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                    .map(Post::getPostId)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Set<Integer>> getOwnedPostByUserId(int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User u = optUser.get();
                Set<Post> ownedPosts = u.getOwnedPosts();
                Set<Integer> ids = new HashSet<>();
                for (Post p : ownedPosts) {
                    ids.add(p.getPostId());
                }
                return ResponseEntity.ok(ids);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Set<Integer>> getLikedPostByUserId(int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User u = optUser.get();
                Set<Post> ownedPosts = u.getLikedPosts();
                Set<Integer> ids = new HashSet<>();
                for (Post p : ownedPosts) {
                    ids.add(p.getPostId());
                }
                return ResponseEntity.ok(ids);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Set<Integer>> getSavedPostsByUserId(int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User u = optUser.get();
                Set<Post> ownedPosts = u.getSavedPosts();
                Set<Integer> ids = new HashSet<>();
                for (Post p : ownedPosts) {
                    ids.add(p.getPostId());
                }
                return ResponseEntity.ok(ids);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<Boolean> likePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getLikers() == null) p.setLikers(new HashSet<>());
                if (u.getLikedPosts() == null) u.setLikedPosts(new HashSet<>());

                if (p.getLikers().contains(u)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                p.getLikers().add(u);
                u.getLikedPosts().add(p);
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<Boolean> savePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getSavers() == null) p.setSavers(new HashSet<>());
                if (u.getSavedPosts() == null) u.setSavedPosts(new HashSet<>());

                if (p.getSavers().contains(u)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                p.getSavers().add(u);
                u.getSavedPosts().add(p);
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<Boolean> unlikePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getLikers() != null && u.getLikedPosts() != null && p.getLikers().contains(u)) {
                    p.getLikers().remove(u);
                    u.getLikedPosts().remove(p);
                    // No need to save explicitly, @Transactional will handle it
                    return ResponseEntity.ok(true);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public ResponseEntity<Boolean> unSavePost(int postId, int userId) {
        try {
            Optional<Post> optPost = postRepository.findById(postId);
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent() && optPost.isPresent()) {
                Post p = optPost.get();
                User u = optUser.get();
                if (p.getSavers() != null && u.getSavedPosts() != null && p.getSavers().contains(u)) {
                    p.getSavers().remove(u);
                    u.getSavedPosts().remove(p);
                    // No need to save explicitly, @Transactional will handle it
                    return ResponseEntity.ok(true);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<PostSummary> createPost(CreatePostRequestUrl request, int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            User u;
            if (optUser.isPresent()) {
                u = optUser.get();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
            return ResponseEntity.ok(new PostSummary(savedPost));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<PostSummary> createPost(CreatePostRequestImages requestImages, int userId) {
        try {
            Optional<User> optUser = userRepository.findById(userId);
            User u;
            if (optUser.isPresent()) {
                u = optUser.get();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Post post = new Post();
            post.setDescription(requestImages.getDescription());
            post.setUser(u);
            post.setCreatedAt(requestImages.getCreatedAt() != null ? requestImages.getCreatedAt() : Instant.now());
            Set<PostImage> postImages = new HashSet<>();
            for (MultipartFile image : requestImages.getImages()) {
                String imageUrl = fileUploadService.uploadFile(image);
                PostImage postImage = new PostImage();
                postImage.setImageUrl(imageUrl);
                postImage.setPost(post);
                postImages.add(postImage);
            }
            post.setImages(postImages);
            Post savedPost = postRepository.save(post);
            return ResponseEntity.ok(new PostSummary(savedPost));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
