package spring.admintgbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import spring.admintgbot.util.LoggerToTgChat;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WelcomeService {

    private final MessageService messageService;
    private final LoggerToTgChat logger;
    private final Map<Long, String> welcomeMessages;


    public void welcomeNewMembers(long chatId, List<User> members) {
        var tpl = welcomeMessages.get(chatId);
        if (tpl == null || members == null || members.isEmpty()) return;

        for (User user : members) {
            String userName = (user.getUserName() != null) ? "@" + user.getUserName() : user.getFirstName();
            String text = tpl.replace("{username}", userName);

            Integer messageId = messageService.sendMsg(chatId, text);
            if (messageId != null) {
                messageService.delMsgLater(chatId, messageId, 1800);
            }
            logger.log("Добавлен новый пользователь " + userName + ", в чате " + chatId);
        }
    }

}
