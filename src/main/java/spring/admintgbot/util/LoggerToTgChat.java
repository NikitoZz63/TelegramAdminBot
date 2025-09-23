package spring.admintgbot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class LoggerToTgChat {
    private final @Lazy TelegramClient client;
    private final AtomicInteger counter = new AtomicInteger();
    @Value("${bot.log-chat-id}")
    private long logChatId;

    public void log(String text) {
        String msg = counter.incrementAndGet() + ") [" + LocalDateTime.now() + "] " + text;
        try {
            client.execute(SendMessage.builder()
                    .chatId(logChatId)
                    .text(msg)
                    .parseMode("HTML")
                    .build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
