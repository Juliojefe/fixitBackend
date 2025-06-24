package com.example.fixit.dto;

import com.example.fixit.model.Chat;
import com.example.fixit.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatSummary {
    private String name;
    private Set<String> userNames;

    public ChatSummary(String name, Set<String> userNames) {
        this.name = name;
        this.userNames = userNames;
    }

    public ChatSummary(Chat chat) {
        this.name = chat.getName();
        Set<String> userNames = new HashSet<>();
        for (User u : chat.getUsers()) {
            userNames.add(u.getName());
        }
        this.userNames = userNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(Set<String> userNames) {
        this.userNames = userNames;
    }
}