package com.example.fixit.repository;
import com.example.fixit.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    Optional<Chat> findById(int chatId);
}