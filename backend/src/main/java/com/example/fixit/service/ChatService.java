package com.example.fixit.service;

import com.example.fixit.dto.response.ChatSummary;
import com.example.fixit.model.Chat;
import com.example.fixit.model.User;
import com.example.fixit.repository.ChatRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<ChatSummary> getChatSummaryById(int chatId, User user) {
        try {
            Chat chat = chatRepository.findById(chatId).orElse(null);
            if (chat == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ChatSummary());
            }
            if (!chat.getUsers().contains(user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(new ChatSummary(chat));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<ChatSummary> createChat(String name, User currentUser, Set<Integer> userIds) {
        try {
            Chat chat = new Chat();
            chat.setName(name);
            chat.getUsers().add(currentUser);

            for (Integer id : userIds) {
                Optional<User> userOptional = userRepository.findById(id);
                userOptional.ifPresent(chat.getUsers()::add);
            }

            chatRepository.save(chat);
            return ResponseEntity.ok(new ChatSummary(chat));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Set<ChatSummary>> getChatsForUser(User user) {
        try {
            Set<ChatSummary> chatSummaries = user.getChats().stream()
                    .map(ChatSummary::new)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(chatSummaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}