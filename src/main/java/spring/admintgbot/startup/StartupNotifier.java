package spring.admintgbot.startup;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import spring.admintgbot.config.WelcomeProperties;
import spring.admintgbot.util.LoggerToTgChat;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StartupNotifier {

    private final LoggerToTgChat logger;
    private final WelcomeProperties props;
    private final Map<Long, String> welcomeMessages;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        props.getTemplates().forEach((chatId, filename) -> {
            String body = welcomeMessages.get(chatId);
            if (body == null) {
                logger.log("Ошибка при загрузке шаблона. Файл: " + filename + ", Chat id: " + chatId);
            }
        });
    }
}
