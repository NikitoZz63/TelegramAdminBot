package spring.admintgbot.config;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TelegramConfig {


    private static final Logger log = LoggerFactory.getLogger(TelegramConfig.class);

    // HTTP‑клиент, нужен телеграм‑клиенту
    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    // единый TelegramClient для сервисов (MessageService/UserManager(отправлять, удалять сообщения и др))
    @Bean
    TelegramClient telegramClient(OkHttpClient ok, BotProperties props) {
        return new OkHttpTelegramClient(ok, props.getToken());
    }

    @Bean
    TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public Map<Long, String> welcomeMessages(WelcomeProperties props) {
        HashMap<Long, String> map = new HashMap<>();
        props.getTemplates().forEach((chatId, filename) -> {
            try (var is = getClass().getClassLoader().getResourceAsStream("templates/" + filename)) {
                if (is == null) {
                    log.error("Templates не найден:{}", filename);
                    throw new IllegalStateException("Templates не найден:" + filename);
                }
                var content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                map.put(chatId, content);
            } catch (Exception e) {
                log.error("Ошибка при загрузке шаблона: {}{}{}", filename, chatId, e.getMessage());
                throw new RuntimeException(e);
            }
        });

        return Collections.unmodifiableMap(map);
    }

}
