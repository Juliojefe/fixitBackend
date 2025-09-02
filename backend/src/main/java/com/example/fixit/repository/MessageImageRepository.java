package com.example.fixit.repository;
import com.example.fixit.model.MessageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface MessageImageRepository extends JpaRepository<MessageImage, Integer> {
    List<MessageImage> findByMessageMessageId(Integer messageId);   //  get images from a message by the message's id
}