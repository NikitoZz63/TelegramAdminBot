package spring.admintgbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@Getter
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    private String token;
    private Long logChatId;

}
