package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.KeyboardFactory.createCountKeyboard;
import static org.example.KeyboardFactory.createTimeKeyboard;

public class TelegramBot extends TelegramLongPollingBot {
    private final Map<Long, List<UserMessage>> messagesMap = new HashMap<>();
    private final MessageSummarizer summarizer = new MessageSummarizer();

    public void cleaner() {
        MessageCleaner cleaner = new MessageCleaner(messagesMap);
        Timer timer = new Timer();
        timer.schedule(cleaner, 0, 1000 * 60 * 60 * 12);
    }

    public TelegramBot() {
        super();
        cleaner();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Runnable runnable = () -> {
            SendMessage answer = new SendMessage();
            boolean toSendMessage = false;
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                String firstName = message.getFrom().getFirstName();
                String lastName = message.getFrom().getLastName() == null ? "" : message.getFrom().getLastName();
                String userName = firstName + " " + lastName;

                Long chatId = message.getChatId();
                String text = message.getText();
                LocalDateTime time = LocalDateTime.now();

                answer.setChatId(update.getMessage().getFrom().getId());
                if (!messagesMap.containsKey(chatId)) {
                    messagesMap.put(chatId, new ArrayList<>());
                }

                if (!message.isCommand()) {
                    messagesMap.get(chatId).add(new UserMessage(userName, text, time));
                }

                if (update.getMessage().getText().equals("/sumbytime")) {
                    answer.setText("For how many minutes would you like the summary? Please choose a number.");
                    answer.setReplyMarkup(createTimeKeyboard(chatId));
                    toSendMessage = true;
                } else if (update.getMessage().getText().equals("/sumbycount")) {
                    answer.setText("For how many messages would you like the summary? Please choose a number.");
                    answer.setReplyMarkup(createCountKeyboard(chatId));
                    toSendMessage = true;
                } else if (update.getMessage().getText().equals("/clear")) {
                    messagesMap.get(chatId).clear();
                    answer.setText("Chat history cleared");
                    toSendMessage = true;
                }
            } else if (update.hasCallbackQuery()) {
                CallbackQuery query = update.getCallbackQuery();
                Long chatId = query.getMessage().getChatId();
                toSendMessage = true;
                deleteMessage(chatId, query.getMessage().getMessageId());
                answer.setChatId(chatId);
                answer.setText("The chat contains no messages or error has occurred ");
                String[] parts = query.getData().split(",");
                String filterAmount = parts[0].split(":")[1].trim();
                String chatIdPart = parts[1].split(":")[1].trim();

                if (!messagesMap.isEmpty()) {
                    List<String> messageList = new ArrayList<>();
                    try {
                        execute(SendMessage.builder().chatId(chatId).text("Summarizing...").build());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    if (query.getData().contains("time:")) {
                        messageList = messageFormatter(messageTimeFilter(messagesMap, Long.valueOf(chatIdPart), Integer.parseInt(filterAmount)));
                    } else if (query.getData().contains("count:")) {
                        messageList = messageFormatter(messageCountFilter(messagesMap, Long.valueOf(chatIdPart), Integer.parseInt(filterAmount)));
                    }
                    if (!messageList.isEmpty()) {
                        answer.setText(summarizer.summarize(messageList));
                    } else {
                        answer.setText("The chat contains no messages");
                    }

                }
            }
            if (toSendMessage) {
                try {
                    execute(answer);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        new Thread(runnable).start();
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        try {
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> messageFormatter(List<UserMessage> userMessageList) {
        return userMessageList.stream().map(m -> String.format("%s: %s", m.getUsername(), m.getText())).toList();
    }

    List<UserMessage> messageTimeFilter(Map<Long, List<UserMessage>> map, Long chatId, int min) {
        if ( map.get(chatId)==null) {
            return Collections.emptyList();
        }
        LocalDateTime filterTime = LocalDateTime.now().minusMinutes(min);
        return  Optional.of(map.get(chatId).stream().filter(m -> m.getTimeSent().isAfter(filterTime)).toList()).orElse(Collections.emptyList());
    }

    List<UserMessage> messageCountFilter(Map<Long, List<UserMessage>> map, Long chatId, int count) {
        if (count <= 0 || map.get(chatId)==null) {
            return Collections.emptyList();
        }
        return Optional.of(map.get(chatId).stream()
                .limit(count)
                .toList()).orElse(Collections.emptyList());
}

public Map<Long, List<UserMessage>> getMessagesMap() {
    return messagesMap;
}

@Override
public String getBotUsername() {
    return "summerAIzerBot";
}

@Override
public String getBotToken() {
    return "";
}
}