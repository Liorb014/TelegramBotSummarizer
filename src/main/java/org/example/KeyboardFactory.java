package org.example;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;
public class KeyboardFactory {
    public static InlineKeyboardMarkup createKeyboard(String type, List<String> labels, Long chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> columns = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (String label : labels) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(label);
            button.setCallbackData(type + ": " + extractValue(label) + ",chatId:" + chatId);
            currentRow.add(button);
            if (currentRow.size() == 3) {
                columns.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }
        if (!currentRow.isEmpty()) {
            columns.add(currentRow);
        }

        keyboardMarkup.setKeyboard(columns);
        return keyboardMarkup;
    }

    private static String extractValue(String label) {
        return label.replaceAll("[^0-9]", "").trim();
    }

    public static InlineKeyboardMarkup createTimeKeyboard(Long chatId) {
        List<String> timeLabels = List.of("5 Minute", "15 Minutes", "30 Minutes", "45 Minutes", "60 Minutes", "90 Minutes", "120 Minutes", "150 Minutes", "180 Minutes");
        return createKeyboard("time", timeLabels, chatId);
    }

    public static InlineKeyboardMarkup createCountKeyboard(Long chatId) {
        List<String> countLabels = List.of("25 messages", "50 messages", "100 messages", "250 messages", "350 messages", "500 messages");
        return createKeyboard("count", countLabels, chatId);
    }
}
