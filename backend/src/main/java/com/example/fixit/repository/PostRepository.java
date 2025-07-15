package com.example.fixit.repository;
import com.example.fixit.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findById(int postId);
}