package Handlers;

import DAO.UserDAO;
import Logger.LoggerToTgChat;
import Services.MessageService;
import Services.UserManager;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.regex.Pattern;

public class ViolationHandler {
    private static final LoggerToTgChat tgLogger = LoggerToTgChat.getInstance();
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)\\b(?:https?://|www\\.|t\\.me/|bit\\.ly/|[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})\\S*"
    );
    private static final long LOG_CHAT_ID = -4762815401L;
    private final MessageService messageService;
    private final UserManager userManager;

    public ViolationHandler(MessageService messageService, UserManager userManager) {
        this.messageService = messageService;
        this.userManager = userManager;
    }

    boolean containsLink(String text) {
        return URL_PATTERN.matcher(text).find();
    }

    public void processLinkViolation(Update update, long chatId, long userId, Message message) {
        String username = (message.getFrom().getUserName() != null) ? "@" + message.getFrom().getUserName() : message.getFrom().getFirstName();

        if (!userManager.isAdmin(userId, chatId)) {

            messageService.delMsg(chatId, message.getMessageId());
            userManager.setUserEntity(update);

            if ((UserDAO.getViolationCounter(userId) > 1)) {
                userManager.baneUser(chatId, userId);
                tgLogger.log("Забанен пользователь " + username, LOG_CHAT_ID);
                messageService.sendMsg(chatId, "<b>Пользователь</b> " + username + " <b> забанен за размещение ссылки</b>");
            } else {
                userManager.muteUser(chatId, userId);
                tgLogger.log("Mute 24 часа  " + username, LOG_CHAT_ID);
                messageService.sendMsg(chatId, username + " ❗ Предупреждение ❗\n" +
                        "<b>Вы нарушили правила нашего чата, поэтому ваша возможность отправлять сообщения ограничена на 24 часа.</b>\n" +
                        "Пожалуйста, ознакомьтесь с правилами.");
            }
        }
    }
}
