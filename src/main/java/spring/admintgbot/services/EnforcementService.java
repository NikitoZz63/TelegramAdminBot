package spring.admintgbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import spring.admintgbot.util.LoggerToTgChat;

@Service
@RequiredArgsConstructor
public class EnforcementService {

    private final TelegramClient telegramClient;
    private final LoggerToTgChat logger;

    public void banForever(long chatId, long userId) {
        BanChatMember banChatMember = new BanChatMember(String.valueOf(chatId), userId);
        try {
            telegramClient.execute(banChatMember);
        } catch (TelegramApiException e) {
            logger.log("Ошибка бана " + userId + " в чате " + chatId + ". Ошибка: " + e.getMessage());
        }
    }

    public void muteFor(Long chatId, Long userId, int seconds) {
        int untilDate = (int) (System.currentTimeMillis() / 1000 + seconds);
        ChatPermissions permissions = new ChatPermissions();
        permissions.setCanSendMessages(false);

        RestrictChatMember restrictRequest = new RestrictChatMember(String.valueOf(chatId), userId, permissions);
        restrictRequest.setUntilDate(untilDate);

        try {
            telegramClient.execute(restrictRequest);
        } catch (TelegramApiException e) {
            logger.log("Ошибка Мута на 24 часа в чате " + chatId + ". Ошибка: " + e.getMessage());
        }
    }

}
