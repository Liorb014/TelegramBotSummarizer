package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private final Map<Long, List<UserMessage>> messagesMap = new HashMap<>();
    private final MessageSummarizer summarizer = new MessageSummarizer();

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage answer = new SendMessage();
        DeleteMessage deleteMessage = new DeleteMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String firstName = message.getFrom().getFirstName();
            String lastName = message.getFrom().getLastName() == null ? "" : message.getFrom().getLastName();
            String userName = STR."\{firstName} \{lastName}";

            Long chatId = message.getChatId();
            String text = message.getText();
            LocalDateTime time = LocalDateTime.now();

//    answer.setChatId(chatId);
            answer.setChatId(update.getMessage().getFrom().getId());
            if (!messagesMap.containsKey(chatId)) {
                messagesMap.put(chatId, new ArrayList<>());
            }

            if (!message.isCommand()) {
                messagesMap.get(chatId).add(new UserMessage(userName, text, time));
            }

            if (update.getMessage().getText().equals("/a")) {
                List<String> messageList = messageFormatter(messagesMap.get(chatId));
                answer.setText(summarizer.summarize(messageList));

            } else if (update.getMessage().getText().equals("/sumbytime")) {
                answer.setText("For how many minutes would you like the summary? Please choose a number.");
                answer.setReplyMarkup(createTimeKeyboard(chatId));
            } else if (update.getMessage().getText().equals("/sumbycount")) {
                answer.setText("For how many messages would you like the summary? Please choose a number.");
                answer.setReplyMarkup(createCountKeyboard(chatId));
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            Long chatId = query.getMessage().getChatId();
//            answer.setChatId(chatId);
            answer.setChatId(update.getCallbackQuery().getFrom().getId());
            answer.setText("error");
            if (query.getData().contains("time:")) {
//                int time = Integer.parseInt(query.getData().substring("time:".length()).trim());
                String[] parts = query.getData().split(",");
                String timePart = parts[0].split(":")[1].trim();
                String chatIdPart = parts[1].split(":")[1].trim();

                if (!messagesMap.isEmpty()) {
                    List<String> messageList = messageFormatter(messageTimeFilter(messagesMap, Long.valueOf(chatIdPart),  Integer.parseInt(timePart)));
                    answer.setText(summarizer.summarize(messageList));
                }
            } else if (query.getData().contains("count:")) {
//                int count = Integer.parseInt(query.getData().substring("count:".length()).trim());
                String[] parts = query.getData().split(",");
                String count = parts[0].split(":")[1].trim();
                String chatIdPart = parts[1].split(":")[1].trim();
                if (!messagesMap.isEmpty()) {
                    List<String> messageList = messageFormatter(messageCountFilter(messagesMap, Long.valueOf(chatIdPart), Integer.parseInt(count)));
                    answer.setText(summarizer.summarize(messageList));
                }
            }
        }
        try {
            execute(answer);
            if (update.hasCallbackQuery()) {
                deleteMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                execute(deleteMessage);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> messageFormatter(List<UserMessage> userMessageList) {
        return userMessageList.stream().map(m -> STR."\{m.getUsername()}: \{m.getText()}").toList();
    }

    private List<UserMessage> messageTimeFilter(Map<Long, List<UserMessage>> map, Long chatId, int min) {
        LocalDateTime filterTime = LocalDateTime.now().minusMinutes(min);
        return map.get(chatId).stream().filter(m -> m.getTimeSent().isAfter(filterTime)).toList();
    }

    private List<UserMessage> messageCountFilter(Map<Long, List<UserMessage>> map, Long chatId, int count) {
        return map.get(chatId).stream().limit(count).toList();
    }


    public static InlineKeyboardMarkup createTimeKeyboard(Long chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> columns = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("5 Minute");
        button1.setCallbackData("time: 5,chatId:"+chatId);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("15 Minutes");
        button2.setCallbackData("time: 15,chatId:"+chatId);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("30 Minutes");
        button3.setCallbackData("time: 30,chatId:"+chatId);

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("45 Minutes");
        button4.setCallbackData("time: 45,chatId:"+chatId);

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("60 Minutes");
        button5.setCallbackData("time: 60,chatId:"+chatId);

        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("90 Minutes");
        button6.setCallbackData("time: 90,chatId:"+chatId);

        InlineKeyboardButton button7 = new InlineKeyboardButton();
        button7.setText("120 Minutes");
        button7.setCallbackData("time: 120,chatId:"+chatId);

        InlineKeyboardButton button8 = new InlineKeyboardButton();
        button8.setText("150 Minutes");
        button8.setCallbackData("time: 150,chatId:"+chatId);

        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText("180 Minutes");
        button9.setCallbackData("time: 180,chatId:"+chatId);

        rowInline1.add(button1);
        rowInline1.add(button2);
        rowInline1.add(button3);

        rowInline2.add(button4);
        rowInline2.add(button5);
        rowInline2.add(button6);

        rowInline3.add(button7);
        rowInline3.add(button8);
        rowInline3.add(button9);

        columns.add(rowInline1);
        columns.add(rowInline2);
        columns.add(rowInline3);

        keyboardMarkup.setKeyboard(columns);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup createCountKeyboard(Long chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> columns = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("25 messages");
        button1.setCallbackData("count: 25,chatId:"+chatId);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("50 messages");
        button2.setCallbackData("count: 50,chatId:"+chatId);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("100 messages");
        button3.setCallbackData("count: 100,chatId:"+chatId);

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("250 messages");
        button4.setCallbackData("count: 250,chatId:"+chatId);

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("350 messages");
        button5.setCallbackData("count: 350,chatId:"+chatId);

        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("500 messages");
        button6.setCallbackData("count: 500,chatId:"+chatId);

        rowInline1.add(button1);
        rowInline1.add(button2);
        rowInline1.add(button3);

        rowInline2.add(button4);
        rowInline2.add(button5);
        rowInline2.add(button6);

        columns.add(rowInline1);
        columns.add(rowInline2);

        keyboardMarkup.setKeyboard(columns);

        return keyboardMarkup;
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