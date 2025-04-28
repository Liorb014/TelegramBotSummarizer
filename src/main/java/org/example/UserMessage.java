package org.example;

import java.time.LocalDateTime;

public class UserMessage {
    private String username;
    private String text;
    private LocalDateTime timeSent;

    // Constructor
    public UserMessage(String username, String text, LocalDateTime timeSent) {
        this.username = username;
        this.text = text;
        this.timeSent = timeSent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }
}