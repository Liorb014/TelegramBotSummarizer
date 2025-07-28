package org.example;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserMessage that = (UserMessage) o;

        if (!Objects.equals(username, that.username)) return false;
        if (!Objects.equals(text, that.text)) return false;
        return Objects.equals(timeSent, that.timeSent);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (timeSent != null ? timeSent.hashCode() : 0);
        return result;
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