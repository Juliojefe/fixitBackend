package com.example.fixit.service;

import com.example.fixit.dto.response.MessageDTO;
import com.example.fixit.model.Chat;
import com.example.fixit.model.Message;
import com.example.fixit.model.User;
import com.example.fixit.model.MessageImage;
import com.example.fixit.repository.ChatRepository;
import com.example.fixit.repository.MessageRepository;
import com.example.fixit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageImageService messageImageService;

//    public MessageDTO saveMessage(int chatId, String content, String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
//        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
//        if (!chat.getUsers().contains(user)) {
//            throw new RuntimeException("User not in chat");
//        }
//        Message message = new Message();
//        message.setContent(content);
//        message.setUser(user);
//        message.setChat(chat);
//        Message saved = messageRepository.save(message);
//        return mapToDTO(saved);
//    }

    public MessageDTO saveMessage(int chatId, String content, String email, List<String> imageUrls) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        if (!chat.getUsers().contains(user)) {
            throw new RuntimeException("User not in chat");
        }
        Message message = new Message();
        message.setContent(content);
        message.setUser(user);
        message.setChat(chat);
        Message saved = messageRepository.save(message);
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String url : imageUrls) {
                messageImageService.addImage(saved.getMessageId(), url);
            }
        }
        return mapToDTO(saved);
    }

    public List<MessageDTO> getMessagesByChatId(int chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return messageRepository.findByChatChatId(chatId, pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO mapToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setContent(message.getContent());
        dto.setUserId(message.getUser().getUserId());
        dto.setChatId(message.getChat().getChatId());
        dto.setCreatedAt(message.getCreatedAt());
        // Fetch images (via service or repo)
        dto.setImageUrls(messageImageService.getImagesByMessageId(message.getMessageId()).stream()
                .map(MessageImage::getImageUrl)
                .collect(Collectors.toList()));
        return dto;
    }
}