package spring.admintgbot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import spring.admintgbot.config.SecurityProperties;
import spring.admintgbot.util.LoggerToTgChat;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityGuard {

    private final TelegramClient telegramClient;
    private final SecurityProperties security;
    private final LoggerToTgChat logger;

    private Long cachedBotId;


    //получаем id бота для(что бы не было самобана)
    private long botId() {
        if (cachedBotId != null) return cachedBotId;
        try {
            var me = telegramClient.execute(new GetMe());
            cachedBotId = me.getId();
            return cachedBotId;
        } catch (TelegramApiException e) {
            logger.log("Не удалось выполнить getMe(): " + e.getMessage());
            return -1L;
        }
    }

    //проверяем права бота
    private boolean botCanRestrict(long chatId) {
        try {
            var req = new GetChatMember(String.valueOf(chatId), botId());
            var cm = telegramClient.execute(req);

            if (cm instanceof ChatMemberAdministrator administrator) {
                Boolean canRestrict = administrator.getCanRestrictMembers();
                return Boolean.TRUE.equals(canRestrict);
            }
            return false;
        } catch (TelegramApiException e) {
            logger.log("Ошибка прав бота в чате" + chatId + ": " + e.getMessage());
            return false;
        }
    }


    //получаем всех админов чата
    private Set<Long> getAdminIds(long chatId) {
        try {
            var req = new GetChatAdministrators(String.valueOf(chatId));
            var members = telegramClient.execute(req);
            return members.stream()
                    .map(cm -> cm.getUser().getId())
                    .collect(Collectors.toSet());
        } catch (TelegramApiException e) {
            logger.log("Ошибка получения админов чата" + chatId + ": " + e.getMessage());
            return Collections.emptySet();
        }
    }

    public boolean canEnforce(long chatId, User offender) {
        if (offender == null) {
            logger.log("Нарушитель не определен. Чат: " + chatId);
            return false;
        }

        if (offender.getId().equals(botId())) {
            logger.log("Игнор, попытка наказать самого бота");
            return false;
        }

        if (security.isProtectAdmins()) {
            var adminIds = getAdminIds(chatId);
            if (adminIds.contains(offender.getId())) {
                logger.log("Игнор, нарушитель админ! " + printable(offender));
                return false;
            }
        }

        if (!botCanRestrict(chatId)) {
            logger.log("У бота не прав в чате" + chatId);
            return false;
        }

        return true;
    }


    private String printable(User user) {
        if (user == null) return "unknow user";
        return (user.getUserName() == null || user.getUserName().isEmpty()) ? user.getFirstName() : user.getUserName();
    }
}
