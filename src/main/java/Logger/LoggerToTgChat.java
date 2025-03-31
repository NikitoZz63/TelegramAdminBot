package Logger;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;

public class LoggerToTgChat {
    private static LoggerToTgChat instance;
    private static LoggerToTgChat myLogger;
    private static int counter;
    private final TelegramClient telegramClient = new OkHttpTelegramClient("7071269675:AAFobbl5xTLUQsbnWQ7F8li_PyzqGvWUnoc");
    LocalDateTime currentDateTime = LocalDateTime.now();

    public LoggerToTgChat() {
    }

    public static LoggerToTgChat getInstance() {
        if (instance == null) {
            instance = new LoggerToTgChat();
        }
        return instance;
    }

    public void log(String msg, long chatId) {
//        System.out.println(counter++ + ") [" + currentDateTime + "] " + msg);

        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(counter++ + ") [" + currentDateTime + "] " + msg)
                .parseMode("HTML")
                .build();

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
