package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private final Map<Long, List<String>> chatMessages = new HashMap<>();
    private final MessageSummarizer summarizer = new MessageSummarizer();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            String firstName =message.getFrom().getFirstName();
            String lastName = message.getFrom().getLastName()==null?  "": message.getFrom().getLastName();
            String userName =firstName + " " +lastName;

            Long chatId = message.getChatId();
            String text = message.getText();

            if (!chatMessages.containsKey(chatId)) {
                chatMessages.put(chatId, new ArrayList<>());
            }

            if (!message.isCommand()){
                chatMessages.get(chatId).add(userName+ " : "+text );
            }


            System.out.println(update.getMessage().getText());
            System.out.println(userName);

            System.out.println(chatMessages.get(chatId).toString());



if (update.getMessage().getText().equals("/a")){
    String summary = summarizer.summarize(chatMessages.get(chatId));
    sendSummary(chatId, summary , message.getMessageId());
    chatMessages.get(chatId).clear();
}
//            // Check if we should summarize (every 10 messages)
//            if (chatMessages.get(chatId).size() >= 3) {
//                String summary = summarizer.summarize(chatMessages.get(chatId));
//                sendSummary(chatId, summary);
//                chatMessages.get(chatId).clear();
//            }
        }
    }

    private void sendSummary(Long chatId, String summary, Integer replyToMessageId) {
        try {
             SendMessage message = new SendMessage();
             message.setChatId(chatId);
             message.setText(   "📚 Summary:\n" + summary);
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