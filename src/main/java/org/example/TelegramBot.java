package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private final Map<Long, List<UserMessage>> aa = new HashMap<>();
    private final MessageSummarizer summarizer = new MessageSummarizer();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            String firstName = message.getFrom().getFirstName();
            String lastName = message.getFrom().getLastName() == null ? "" : message.getFrom().getLastName();
            String userName = firstName + " " + lastName;

            Long chatId = message.getChatId();
            String text = message.getText();
            LocalDateTime time = LocalDateTime.now();

            if (!aa.containsKey(chatId)) {
                aa.put(chatId, new ArrayList<>());
            }

            if (!message.isCommand()) {
                aa.get(chatId).add(new UserMessage(userName, text, time));
            }

            System.out.println(update.getMessage().getText());


            if (update.getMessage().getText().equals("/a")) {
              List<String> messageList=  aa.get(chatId).stream().map(m -> m.getUsername() +": "+ m.getText()).toList();
                String summary = summarizer.summarize(messageList);
                sendSummary(chatId, summary, message.getMessageId());
            }
        }
    }

    private void sendSummary(Long chatId, String summary, Integer replyToMessageId) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("📚 Summary:\n" + summary);
            message.setReplyToMessageId(replyToMessageId);
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "summerAIzerBot";
    }

    @Override
    public String getBotToken() {
        return "7368179475:AAEI3l7O00NwawPYueDsxk7mNvftIXOBCXY";
    }
}