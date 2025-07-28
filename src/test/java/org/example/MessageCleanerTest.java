package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class MessageCleanerTest {

    private MessageCleaner messageCleaner;
    private Map<Long, List<UserMessage>> messagesMap;


    @BeforeEach
    void setUp() {
        messagesMap = new HashMap<>();
        messageCleaner = new MessageCleaner(messagesMap);
    }

    @Test
    void testRun_NoMessages() {
        messageCleaner.run();
        assertTrue(messagesMap.isEmpty());
    }

    @Test
    void testRun_AllMessagesAreOld() {
        long chatId1 = 1L;
        long chatId2 = 2L;

        messagesMap.put(chatId1, new ArrayList<>(List.of(
                new UserMessage("name","Old message 1", LocalDateTime.now().minusHours(25)),
                new UserMessage("name","Old message 2", LocalDateTime.now().minusHours(30))
        )));
        messagesMap.put(chatId2, new ArrayList<>(List.of(
                new UserMessage("name","Old message 3", LocalDateTime.now().minusHours(48))
        )));

        messageCleaner.run();

        assertTrue(messagesMap.get(chatId1).isEmpty());
        assertTrue(messagesMap.get(chatId2).isEmpty());

    }

    @Test
    void testRun_AllMessagesAreNew() {
        long chatId1 = 1L;
        long chatId2 = 2L;

        UserMessage newMessage1 = new UserMessage("name","New message 1", LocalDateTime.now().minusHours(1));
        UserMessage newMessage2 = new UserMessage("name","New message 2", LocalDateTime.now().minusHours(12));
        UserMessage newMessage3 = new UserMessage("name","New message 3", LocalDateTime.now().minusMinutes(30));
        UserMessage newMessage4 = new UserMessage("name","New message 4", LocalDateTime.now().minusHours(23));

        messagesMap.put(chatId1, new ArrayList<>(List.of(
                newMessage1,
                newMessage2,
                newMessage4
        )));
        messagesMap.put(chatId2, new ArrayList<>(List.of(
                newMessage3
        )));

        messageCleaner.run();

        assertEquals(3, messagesMap.get(chatId1).size());
        assertTrue(messagesMap.get(chatId1).contains(newMessage1));
        assertTrue(messagesMap.get(chatId1).contains(newMessage2));
        assertTrue(messagesMap.get(chatId1).contains(newMessage4));
        assertEquals(1, messagesMap.get(chatId2).size());
        assertTrue(messagesMap.get(chatId2).contains(newMessage3));
    }

    @Test
    void testRun_MixedMessages() {
        long chatId1 = 1L;
        long chatId2 = 2L;

                UserMessage oldMessage1 = new UserMessage("name","Old message 1", LocalDateTime.now().minusHours(25));
        UserMessage newMessage1 = new UserMessage("name","New message 1", LocalDateTime.now().minusHours(23));
        UserMessage oldMessage2 = new UserMessage("name","Old message 2", LocalDateTime.now().minusDays(2));
        UserMessage newMessage2 = new UserMessage("name","New message 2", LocalDateTime.now().minusHours(10));

        messagesMap.put(chatId1, new ArrayList<>(List.of(
                oldMessage1,
                newMessage1
        )));
        messagesMap.put(chatId2, new ArrayList<>(List.of(
                oldMessage2,
                newMessage2
        )));

        messageCleaner.run();

        assertEquals(1, messagesMap.get(chatId1).size());
        assertTrue(messagesMap.get(chatId1).contains(newMessage1));
        assertFalse(messagesMap.get(chatId1).contains(oldMessage1));
        assertEquals(1, messagesMap.get(chatId2).size());
        assertTrue(messagesMap.get(chatId2).contains(newMessage2));
        assertFalse(messagesMap.get(chatId2).contains(oldMessage2));
    }

    @Test
    void testRun_MultipleChatIdsMixedMessages() {
        long chatId1 = 101L;
        long chatId2 = 102L;
        long chatId3 = 103L;

        UserMessage chat1OldMsg1 = new UserMessage("name","Chat1 Old 1", LocalDateTime.now().minusHours(26));
        UserMessage chat1NewMsg1 = new UserMessage("name","Chat1 New 1", LocalDateTime.now().minusHours(5));
        UserMessage chat1OldMsg2 = new UserMessage("name","Chat1 Old 2", LocalDateTime.now().minusDays(3));
        UserMessage chat2NewMsg1 = new UserMessage("name","Chat2 New 1", LocalDateTime.now().minusHours(1));
        UserMessage chat3OldMsg1 = new UserMessage("name","Chat3 Old 1", LocalDateTime.now().minusHours(24).minusMinutes(1));
        UserMessage chat3NewMsg1 = new UserMessage("name","Chat3 New 1", LocalDateTime.now().minusHours(23).minusMinutes(59));


        messagesMap.put(chatId1, new ArrayList<>(List.of(chat1OldMsg1, chat1NewMsg1, chat1OldMsg2)));
        messagesMap.put(chatId2, new ArrayList<>(List.of(chat2NewMsg1)));
        messagesMap.put(chatId3, new ArrayList<>(List.of(chat3OldMsg1, chat3NewMsg1)));

        messageCleaner.run();

        assertEquals(1, messagesMap.get(chatId1).size());
        assertTrue(messagesMap.get(chatId1).contains(chat1NewMsg1));
        assertFalse(messagesMap.get(chatId1).contains(chat1OldMsg1));
        assertFalse(messagesMap.get(chatId1).contains(chat1OldMsg2));
        assertEquals(1, messagesMap.get(chatId2).size());
        assertTrue(messagesMap.get(chatId2).contains(chat2NewMsg1));
        assertEquals(1, messagesMap.get(chatId3).size());
        assertTrue(messagesMap.get(chatId3).contains(chat3NewMsg1));
        assertFalse(messagesMap.get(chatId3).contains(chat3OldMsg1));
    }
}