package spring.admintgbot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import spring.admintgbot.config.BotProperties;
import spring.admintgbot.services.UserManager;
import spring.admintgbot.services.ViolationDetector;
import spring.admintgbot.services.WelcomeService;
import spring.admintgbot.util.LoggerToTgChat;

@RequiredArgsConstructor
@Component
public class AdminBot implements LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {

    private final TelegramClient telegramClient;
    private final BotProperties props;
    private final UserManager userManager;
    private final LoggerToTgChat logger;
    private final ViolationDetector violationDetector;
    private final WelcomeService welcomeService;


    @Override
    public String getBotToken() {
        return props.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.getMessage() == null) return;

        var msg = update.getMessage();
        // Игнорируем приватные чаты: все проверки/санкции применяются только в группах/супергруппах
        if ("private".equals(msg.getChat().getType())) {
            return;
        }

//        if ("/ping".equals(msg.getText())) {
//            var chatId = msg.getChatId();
//            var messageToSend = SendMessage.builder()
//                    .chatId(chatId)
//                    .text("pong")
//                    .build();
//            try {
//                telegramClient.execute(messageToSend);
//            } catch (TelegramApiException e) {
//                throw new RuntimeException(e);
//            }
//        }

        if (msg.getNewChatMembers() != null && !msg.getNewChatMembers().isEmpty()) {
            welcomeService.welcomeNewMembers(msg.getChatId(), msg.getNewChatMembers());
            return;
        }

        if (msg.hasText()) {
            violationDetector.find(msg).ifPresent(v ->
                    userManager.handleViolation(msg.getChatId(), msg.getFrom(), msg.getMessageId(), v)
            );
        }


    }
}
