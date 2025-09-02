package com.example.fixit.dto;

import com.example.fixit.model.Chat;
import com.example.fixit.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatSummary {
    private Integer chatId;
    private String name;
    private Set<Integer> userIds;

    public ChatSummary() {
        this.chatId = -1;
        this.name = "";
        this.userIds = new HashSet<>();
    }

    public ChatSummary(Chat chat) {
        this.chatId = chat.getChatId();
        this.name = chat.getName();
        Set<Integer> userIds = new HashSet<>();
        for (User u : chat.getUsers()) {
            userIds.add(u.getUserId());
        }
        this.userIds = userIds;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }
}