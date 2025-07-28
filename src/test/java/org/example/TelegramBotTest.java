package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TelegramBotFilterMethodsTest {

    private TelegramBot telegramBot;
    private Map<Long, List<UserMessage>> messagesMap;


    @BeforeEach
    void setUp() {
        telegramBot = new TelegramBot();
        messagesMap = telegramBot.getMessagesMap();
        messagesMap.clear(); //
    }


    @Test
    void testMessageTimeFilter_AllRecent() {
        Long chatId = 1L;
        messagesMap.put(chatId, new ArrayList<>(Arrays.asList(
                new UserMessage("user1", "msg1", LocalDateTime.now().minusMinutes(1)),
                new UserMessage("user2", "msg2", LocalDateTime.now().minusMinutes(3)),
                new UserMessage("user3", "msg3", LocalDateTime.now().minusMinutes(4))
        )));

        List<UserMessage> filtered = telegramBot.messageTimeFilter(messagesMap, chatId, 5);

        assertNotNull(filtered);
        assertEquals(3, filtered.size());
        assertEquals("msg1", filtered.get(0).getText());
        assertEquals("msg2", filtered.get(1).getText());
        assertEquals("msg3", filtered.get(2).getText());
    }

    @Test
    void testMessageTimeFilter_AllOld() {
        Long chatId = 2L;
        messagesMap.put(chatId, new ArrayList<>(Arrays.asList(
                new UserMessage("user4", "msg4", LocalDateTime.now().minusMinutes(11)),
                new UserMessage("user5", "msg5", LocalDateTime.now().minusMinutes(15))
        )));

        List<UserMessage> filtered = telegramBot.messageTimeFilter(messagesMap, chatId, 10);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testMessageTimeFilter_Mixed() {
        Long chatId = 3L;
        UserMessage recent1 = new UserMessage("user6", "recent1", LocalDateTime.now().minusMinutes(2));
        UserMessage old1 = new UserMessage("user7", "old1", LocalDateTime.now().minusMinutes(7));
        UserMessage recent2 = new UserMessage("user8", "recent2", LocalDateTime.now().minusMinutes(4));
        UserMessage old2 = new UserMessage("user9", "old2", LocalDateTime.now().minusMinutes(10));

        messagesMap.put(chatId, new ArrayList<>(Arrays.asList(
                recent1, old1, recent2, old2
        )));

        List<UserMessage> filtered = telegramBot.messageTimeFilter(messagesMap, chatId, 5);

        assertNotNull(filtered);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(recent1));
        assertTrue(filtered.contains(recent2));
        assertFalse(filtered.contains(old1));
        assertFalse(filtered.contains(old2));
    }

    @Test
    void testMessageTimeFilter_ChatIdNotFound() {
        Long nonExistentChatId = 99L;
        List<UserMessage> filtered = telegramBot.messageTimeFilter(messagesMap, nonExistentChatId, 5);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testMessageTimeFilter_EmptyChatHistory() {
        Long emptyChatId = 4L;
        messagesMap.put(emptyChatId, new ArrayList<>());

        List<UserMessage> filtered = telegramBot.messageTimeFilter(messagesMap, emptyChatId, 5);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testMessageTimeFilter_ZeroMinutes() {
        Long chatId = 5L;
        messagesMap.put(chatId, new ArrayList<>(Arrays.asList(
                new UserMessage("u1", "m1", LocalDateTime.now().minusSeconds(1)),
                new UserMessage("u2", "m2", LocalDateTime.now().minusMinutes(1))
        )));

        List<UserMessage> filtered = telegramBot.messageTimeFilter(messagesMap, chatId, 0);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }



    @Test
    void testMessageCountFilter_CountLessThanTotal() {
        Long chatId = 10L;
        messagesMap.put(chatId, new ArrayList<>(Arrays.asList(
                new UserMessage("userA", "msgA", LocalDateTime.now()),
                new UserMessage("userB", "msgB", LocalDateTime.now()),
                new UserMessage("userC", "msgC", LocalDateTime.now()),
                new UserMessage("userD", "msgD", LocalDateTime.now())
        )));

        List<UserMessage> filtered = telegramBot.messageCountFilter(messagesMap, chatId, 2);

        assertNotNull(filtered);
        assertEquals(2, filtered.size());
        assertEquals("msgA", filtered.get(0).getText());
        assertEquals("msgB", filtered.get(1).getText());
    }

    @Test
    void testMessageCountFilter_CountGreaterThanTotal() {
        Long chatId = 11L;
        messagesMap.put(chatId, new ArrayList<>(Arrays.asList(
                new UserMessage("userE", "msgE", LocalDateTime.now()),
                new UserMessage("userF", "msgF", LocalDateTime.now())
        )));

        List<UserMessage> filtered = telegramBot.messageCountFilter(messagesMap, chatId, 5);

        assertNotNull(filtered);
        assertEquals(2, filtered.size());
        assertEquals("msgE", filtered.get(0).getText());
        assertEquals("msgF", filtered.get(1).getText());
    }

    @Test
    void testMessageCountFilter_CountIsZero() {
        Long chatId = 12L;
        messagesMap.put(chatId, new ArrayList<>(List.of(
                new UserMessage("userG", "msgG", LocalDateTime.now())
        )));

        List<UserMessage> filtered = telegramBot.messageCountFilter(messagesMap, chatId, 0);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testMessageCountFilter_ChatIdNotFound() {
        Long nonExistentChatId = 199L;
        List<UserMessage> filtered = telegramBot.messageCountFilter(messagesMap, nonExistentChatId, 3);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testMessageCountFilter_EmptyChatHistory() {
        Long emptyChatId = 13L;
        messagesMap.put(emptyChatId, new ArrayList<>());

        List<UserMessage> filtered = telegramBot.messageCountFilter(messagesMap, emptyChatId, 5);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }

    @Test
    void testMessageCountFilter_NegativeCount() {
        Long chatId = 14L;
        messagesMap.put(chatId, new ArrayList<>(List.of(
                new UserMessage("uX", "mX", LocalDateTime.now())
        )));

        List<UserMessage> filtered = telegramBot.messageCountFilter(messagesMap, chatId, -1);

        assertNotNull(filtered);
        assertTrue(filtered.isEmpty());
    }
}