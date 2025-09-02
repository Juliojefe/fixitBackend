package com.example.fixit.repository;
import com.example.fixit.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatChatId(Integer chatId);
    List<Message> findByChatChatId(Integer chatId, Pageable pageable);  //  chat history by chatId
}