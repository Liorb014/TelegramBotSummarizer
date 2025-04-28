import org.example.TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public static void main(String[] args) {
    try {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TelegramBot());
        System.out.println("Bot started successfully!");
    } catch (Exception e) {
        e.printStackTrace();
    }
}