package handlers;

import DAO.UserDAO;
import com.github.demidko.aot.WordformMeaning;
import logger.LoggerToTgChat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import services.MessageService;
import services.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class ViolationHandler {

    private static final LoggerToTgChat tgLogger = LoggerToTgChat.getInstance();
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?i)\\b(?:https?://|www\\.|t\\.me/|bit\\.ly/|[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})\\S*"
    );
    private static final long LOG_CHAT_ID = -4762815401L;
    private static final Set<String> forbiddenWords = new HashSet<>();
    private static final Set<String> forbiddenLemmas = new HashSet<>();
    private MessageService messageService;
    private UserManager userManager;


    public ViolationHandler(MessageService messageService, UserManager userManager) {
        this.messageService = messageService;
        this.userManager = userManager;
    }

    public ViolationHandler() {

    }

    public boolean containsLink(String text) {
        if (URL_PATTERN.matcher(text).find()) {
            tgLogger.log("Нарушение ссылок: " + text, LOG_CHAT_ID);
            return true;
        }
        return false;
    }

    public void txtToSet() {
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("forbiddenWords.txt")))) {
            while ((line = bufferedReader.readLine()) != null) {
                forbiddenWords.add(line.trim().toLowerCase());
            }
            tgLogger.log("Загрузка запрещённых слов завершена", LOG_CHAT_ID);
        } catch (IOException | NullPointerException e) {
            tgLogger.log("Ошибка загрузки запрещённых слов: " + e.getMessage(), LOG_CHAT_ID);
        }
    }

    public String normalizeText(String input) {
        input = input.replaceAll("x", "х")
                .replaceAll("y", "у")
                .replaceAll("e", "е")
                .replaceAll("o", "о")
                .replaceAll("a", "а")
                .replaceAll("c", "с")
                .replaceAll("p", "р");

        // Убираем лишние символы
        input = input.toLowerCase()
                .replaceAll("[^а-яё]", ""); // убираем все, кроме кириллических

        return input;
    }

    public boolean containsForbiddenWords(String text) {
        String normalized = normalizeText(text);

        var meanings = lookupForMeanings(normalized);


        for (WordformMeaning meaning : meanings) {
            String lemma = meaning.getLemma().toString();
            if (forbiddenWords.contains(lemma)) {
                tgLogger.log("Нарушение запрещенных слов: " + text + ". Сообщение : " + lemma, LOG_CHAT_ID);
                return true;
            }
        }
        return false;
    }


    public void processViolation(Update update, long chatId, long userId, Message message) {
        String username = (message.getFrom().getUserName() != null) ? "@" + message.getFrom().getUserName() : message.getFrom().getFirstName();

        if (!userManager.isAdmin(userId, chatId)) {

            messageService.delMsg(chatId, message.getMessageId());
            userManager.setUserEntity(update);

            if ((UserDAO.getViolationCounter(userId) > 1)) {
                userManager.baneUser(chatId, userId);
                tgLogger.log("Забанен пользователь " + username, LOG_CHAT_ID);
                messageService.sendMsg(chatId, "<b>Пользователь</b> " + username + " <b> забанен за нарушение правил чата</b>");
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
