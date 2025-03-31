package adminTGbot;

import Logger.LoggerToTgChat;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;


public class Main {
    private static final LoggerToTgChat tgLogger = LoggerToTgChat.getInstance();
    private static final long LOG_CHAT_ID = -4762815401L;

    public static void main(String[] args) {
        String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        if (botToken == null || botToken.isEmpty()) {
            throw new IllegalStateException("Ошибка: Переменная окружения TELEGRAM_BOT_TOKEN не задана!");
        }


        try (TelegramBotsLongPollingApplication botsApp = new TelegramBotsLongPollingApplication()) {
            botsApp.registerBot(botToken, new AdminBot());
            tgLogger.log("Бот запущен ", LOG_CHAT_ID);

            Thread.currentThread().join();

        } catch (Exception e) {
            tgLogger.log("Ошибка запуска бота: " + e.getMessage(), LOG_CHAT_ID);
        }

    }
}