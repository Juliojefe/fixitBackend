package com.example.fixit.service;

import com.example.fixit.dto.ChatSummary;
import com.example.fixit.model.Chat;
import com.example.fixit.model.User;
import com.example.fixit.repository.ChatRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public ChatSummary getChatSummaryById(int chatId) {
        try {
            Optional<Chat> optChat = chatRepository.findById(chatId);
            if (optChat.isPresent()) {
                return new ChatSummary(optChat.get());
            } else {
                return new ChatSummary();  // Or throw NotFoundException
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<ChatSummary> getAllChats() {
        try {
            List<Chat> chats = chatRepository.findAll();
            return chats.stream()
                    .map(ChatSummary::new)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Chat createChat(String name, User creator, Set<Integer> userIds) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.getUsers().add(creator);
        for (Integer id : userIds) {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                chat.getUsers().add(userOptional.get());
            }
        }
        return chatRepository.save(chat);
    }

    public Set<ChatSummary> getChatsForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getChats().stream()
                .map(ChatSummary::new)
                .collect(Collectors.toSet());
    }
}