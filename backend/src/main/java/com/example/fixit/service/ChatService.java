package com.example.fixit.service;

import com.example.fixit.dto.ChatSummary;
import com.example.fixit.model.Chat;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.fixit.repository.ChatRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public ChatSummary getChatSummaryById(int chatId) {
        try {
            Optional<Chat> OptChat = chatRepository.findById(chatId);
            if (OptChat.isPresent()) {
                Chat c = OptChat.get();
                return new ChatSummary(c);
            } else {
                return new ChatSummary();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<ChatSummary> getAllChats() {
        try {
            Set<ChatSummary> chtSum = new HashSet<>();
            List<Chat> chats = chatRepository.findAll();
            for (Chat cht : chats) {
                chtSum.add(new ChatSummary(cht));
            }
            return chtSum;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}