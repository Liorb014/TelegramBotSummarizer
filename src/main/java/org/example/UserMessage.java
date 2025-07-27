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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserMessage{");
        sb.append("username='").append(username).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", timeSent=").append(timeSent);
        sb.append('}');
        return sb.toString();
    }
}