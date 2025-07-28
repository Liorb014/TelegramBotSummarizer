package org.example;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class MessageCleaner extends TimerTask {
    private final Map<Long, List<UserMessage>> messagesMap;
    private final int messageExpiryTime =24; // in hours

    public MessageCleaner(Map<Long, List<UserMessage>> messagesMap) {
        this.messagesMap = messagesMap;
    }

    @Override
    public void run() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(messageExpiryTime);
        for (Map.Entry<Long, List<UserMessage>> entry : messagesMap.entrySet()) {
            List<UserMessage> chatMessages = entry.getValue();
            Iterator<UserMessage> iterator = chatMessages.iterator();
            while (iterator.hasNext()) {
                UserMessage message = iterator.next();
                if (message.getTimeSent().isBefore(thresholdTime)) {
                    iterator.remove();
                }
            }
        }
    }
}

