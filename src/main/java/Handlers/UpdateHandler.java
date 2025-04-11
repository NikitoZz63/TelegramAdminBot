package Handlers;

import Services.UserManager;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class UpdateHandler {
    private final ViolationHandler violationHandler;
    private final UserManager userManager;

    public UpdateHandler(ViolationHandler violationHandler, UserManager userManager) {
        this.violationHandler = violationHandler;
        this.userManager = userManager;
    }

    public void hundlerUpdate(Update update) {
        if (!update.hasMessage()) return;

        Message message = update.getMessage();
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();

        if (!userManager.isAllowedChat(chatId)) return;

        if (message.getNewChatMembers() != null) {
            userManager.handleNewChatMembers(chatId, message.getNewChatMembers());
        }

        if (message.hasText() && violationHandler.containsLink(message.getText())) {
            violationHandler.processViolation(update, chatId, userId, message);
        }

        if (message.hasText() && violationHandler.forbiddenWords(message.getText())) {
            violationHandler.processViolation(update, chatId, userId, message);
        }
    }
}
