package spring.admintgbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private boolean skipPrivate = true;
    private boolean protectAdmins = true;

}
