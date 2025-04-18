package services;

import logger.LoggerToTgChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageService {

    private static final int MESSAGE_DELETION_TIMER = 3600; //1 час
    private final TelegramClient telegramClient;
    private final LoggerToTgChat tgLogger;
    private final Long logChatId;


    public MessageService(TelegramClient telegramClient, LoggerToTgChat tgLogger, Long logChatId) {
        this.telegramClient = telegramClient;
        this.tgLogger = tgLogger;
        this.logChatId = logChatId;
    }

    public Integer sendMsg(long chatId, String messageText) {
        SendMessage message = SendMessage
                .builder()
                .chatId(chatId)
                .text(messageText)
                .parseMode("HTML")
                .build();

        for (int i = 0; i < 3; i++) {
            try {
                return telegramClient.execute(message).getMessageId();
            } catch (TelegramApiException e) {
                if (e.getMessage().contains("Too Many Requests")) {
                    int waitTime = extractFloodWaitTime(e.getMessage());
                    tgLogger.log("FloodWait! Ожидание " + waitTime + " сек.", logChatId);
                    try {
                        Thread.sleep(waitTime * 1000L);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    tgLogger.log("Ошибка отправки сообщения: " + e.getMessage(), logChatId);
                }
            }
        }
        return null;
    }

    private int extractFloodWaitTime(String errorMessage) {
        Pattern pattern = Pattern.compile("retry after (\\d+)");
        Matcher matcher = pattern.matcher(errorMessage);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 30; // Если нет информации, ждем 30 сек.
    }

    public void delMsg(long chatId, int messageId) {
        DeleteMessage deleteMessages = new DeleteMessage(String.valueOf(chatId), messageId);

        try {
            telegramClient.execute(deleteMessages);
        } catch (TelegramApiException e) {
            tgLogger.log("Ошибка удаления сообщения " + messageId + ". Ошибка" + e.getMessage(), logChatId);
        }
    }

    void scheduleMessageDeletion(long chatId, Integer messageId) {

        new Thread(() -> {
            try {
                Thread.sleep(MESSAGE_DELETION_TIMER * 1000L);
                delMsg(chatId, messageId);
            } catch (InterruptedException e) {
                tgLogger.log("Ошибка при ожидании удаления сообщения: " + e.getMessage(), logChatId);
            }
        }).start();
    }
}
