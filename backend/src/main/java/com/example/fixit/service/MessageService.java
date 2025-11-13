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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
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

    public ResponseEntity<MessageDTO> saveMessage(int chatId, String content, String email, List<String> imageUrls) {
        try {
            Optional<User> optUser = userRepository.findByEmail(email);
            Optional<Chat> optChat = chatRepository.findById(chatId);
            if (!optUser.isPresent() || !optChat.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDTO());
            }
            User user = optUser.get();
            Chat chat = optChat.get();
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
            return ResponseEntity.ok(mapToDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<MessageDTO>> getMessagesByChatId(int chatId, int page, int size) {
        try {
            if (!chatRepository.existsById(chatId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<MessageDTO> messages = messageRepository.findByChatChatId(chatId, pageable)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private MessageDTO mapToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setContent(message.getContent());
        dto.setUserId(message.getUser().getUserId());
        dto.setChatId(message.getChat().getChatId());
        dto.setCreatedAt(message.getCreatedAt());
        // Fetch images
        dto.setImageUrls(messageImageService.getImagesByMessageId(message.getMessageId()).stream()
                .map(MessageImage::getImageUrl)
                .collect(Collectors.toList()));
        return dto;
    }
}