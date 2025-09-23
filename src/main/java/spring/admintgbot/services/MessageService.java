package spring.admintgbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import spring.admintgbot.util.LoggerToTgChat;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final TelegramClient telegramClient;
    private final TaskScheduler scheduler;
    private final LoggerToTgChat tgLogger;


    public Integer sendMsg(long chatId, String messageText) {
        try {
            return telegramClient.execute(
                    SendMessage.builder()
                            .chatId(chatId)
                            .text(messageText)
                            .parseMode("HTML")
                            .build()
            ).getMessageId();
        } catch (TelegramApiException e) {
            tgLogger.log("Ошибка отправки сообщения " + messageText + ". Ошибка" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void delMsg(long chatId, int messageId) {
        DeleteMessage deleteMessages = new DeleteMessage(String.valueOf(chatId), messageId);
        try {
            telegramClient.execute(deleteMessages);
        } catch (TelegramApiException e) {
            tgLogger.log("Ошибка удаления сообщения " + messageId + ". Ошибка" + e.getMessage());
        }
    }

    public void delMsgLater(long chatId, int messageId, long seconds) {
        scheduler.schedule(() -> delMsg(chatId, messageId),
                Instant.now().plusSeconds(seconds));
    }
}
