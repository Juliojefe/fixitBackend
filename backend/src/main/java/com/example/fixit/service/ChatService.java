package com.example.fixit.service;

import com.example.fixit.dto.response.ChatSummary;
import com.example.fixit.exception.ResourceNotFoundException;
import com.example.fixit.model.Chat;
import com.example.fixit.model.User;
import com.example.fixit.repository.ChatRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ChatSummary getChatSummaryById(int chatId, User user) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("The chat you are looking for was not found"));
        if (!chat.getUsers().contains(user)) {
            throw new ResourceNotFoundException("That user in not a member of the chat");
        }
        return new ChatSummary(chat);
    }

    public ChatSummary createChat(String name, User currentUser, Set<Integer> userIds) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.getUsers().add(currentUser);
        for (Integer id : userIds) {
            Optional<User> userOptional = userRepository.findById(id);
            userOptional.ifPresent(chat.getUsers()::add);
        }
        chatRepository.save(chat);
        return new ChatSummary(chat);
    }

    public Set<ChatSummary> getChatsForUser(User user) {
        return user.getChats().stream().map(ChatSummary::new).collect(Collectors.toSet());
    }
}