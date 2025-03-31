package adminTGbot;

import Handlers.UpdateHandler;
import Handlers.ViolationHandler;
import Handlers.WelcomeMessageProvider;
import Logger.LoggerToTgChat;
import Services.MessageService;
import Services.UserManager;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class AdminBot implements LongPollingSingleThreadUpdateConsumer {
    private static final Long LOG_CHAT_ID = -4762815401L;
    private final UpdateHandler updateHandler;


    public AdminBot() {
        TelegramClient telegramClient = new OkHttpTelegramClient(System.getenv("TELEGRAM_BOT_TOKEN"));
        LoggerToTgChat tgLogger = LoggerToTgChat.getInstance();

        MessageService messageService = new MessageService(telegramClient, tgLogger, LOG_CHAT_ID);
        UserManager userManager = new UserManager(telegramClient, WelcomeMessageProvider.getWelcomMsg(), messageService, tgLogger, LOG_CHAT_ID);
        ViolationHandler violationHandler = new ViolationHandler(messageService, userManager);

        this.updateHandler = new UpdateHandler(violationHandler, userManager);
    }

    @Override
    public void consume(Update update) {
        updateHandler.hundlerUpdate(update);
    }
}
