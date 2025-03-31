package Services;

import DAO.UserDAO;
import Entity.UserEntity;
import Logger.LoggerToTgChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserManager {
    private final TelegramClient telegramClient;
    private final Map<Long, String> welcomeMessages;
    private final MessageService messageService;
    private final LoggerToTgChat tgLogger;
    private final long logChatId;

//    private static final LoggerToTgChat tgLogger = LoggerToTgChat.getInstance();

    public UserManager(TelegramClient telegramClient, Map<Long, String> welcomeMessages, MessageService messageService, LoggerToTgChat tgLogger, long logChatId) {
        this.telegramClient = telegramClient;
        this.welcomeMessages = welcomeMessages;
        this.tgLogger = tgLogger;
        this.logChatId = logChatId;
        this.messageService = messageService;
    }

    public boolean isAllowedChat(long chatId) {
        if (!welcomeMessages.containsKey(chatId)) {
            messageService.sendMsg(chatId, "❌ Этот бот не работает в данном чате.");
            return false;
        }
        return true;
    }

    public boolean isAdmin(long chatId, long userId) {
        return getAdminIdFromChat(chatId).contains(userId);
    }

    private List<Long> getAdminIdFromChat(long chatId) {

        try {
            return telegramClient.execute(new GetChatAdministrators(String.valueOf(chatId)))
                    .stream()
                    .map(chatMember -> chatMember.getUser().getId())
                    .collect(Collectors.toList());
        } catch (TelegramApiException e) {
            tgLogger.log("Ошибка получения ID админов: " + e.getMessage(), logChatId);
            return Collections.emptyList();
        }

    }

    public void setUserEntity(Update update) {
        User user = update.getMessage().getFrom();
        UserEntity userEntity = UserDAO.getUserByTelegramUserId(user.getId());

        if (userEntity == null) {
            userEntity = new UserEntity(
                    user.getFirstName(),
                    user.getId(),
                    user.getUserName(),
                    1
            );
        } else {
            userEntity.setViolationCounter(userEntity.getViolationCounter() + 1);
        }
        UserDAO.saveUser(userEntity);
        tgLogger.log("Пользователь " + user.getUserName() + " обновлён/сохранён с ViolationCounter = " + userEntity.getViolationCounter(), logChatId);
    }

    public void baneUser(long chatId, long userId) {
        BanChatMember banChatMember = new BanChatMember(String.valueOf(chatId), userId);
        try {
            telegramClient.execute(banChatMember);
        } catch (TelegramApiException e) {
            tgLogger.log("Ошибка бана " + userId + " в чате " + chatId + ". Ошибка: " + e.getMessage(), logChatId);
        }
    }

    public void muteUser(Long chatId, Long userId) {
        Integer untilDate = (int) (Instant.now().getEpochSecond() + 86_400);
        ChatPermissions permissions = new ChatPermissions();
        permissions.setCanSendMessages(false);

        RestrictChatMember restrictRequest = new RestrictChatMember(String.valueOf(chatId), userId, permissions);
        restrictRequest.setUntilDate(untilDate);

        try {
            telegramClient.execute(restrictRequest);
        } catch (TelegramApiException e) {
            tgLogger.log("Ошибка Мута на 24 часа в чате " + chatId + ". Ошибка: " + e.getMessage(), logChatId);
        }
    }


    public void handleNewChatMembers(long chatId, List<User> newUserList) {
        for (User user : newUserList) {
            String newUserName = (user.getUserName() != null) ? "@" + user.getUserName() : user.getFirstName();
            String welcomeMessage = welcomeMessages.get(chatId).replace("{username}", newUserName);

            Integer messageId = messageService.sendMsg(chatId, welcomeMessage);
            if (messageId != null) {
                messageService.scheduleMessageDeletion(chatId, messageId); // 24 часа
            }
            tgLogger.log("Добавлен новый пользователь " + newUserName, logChatId);
        }
    }
}

