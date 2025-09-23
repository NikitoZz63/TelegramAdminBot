package spring.admintgbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "welcome")
public class WelcomeProperties {

//    private static final Map<Long, String> TEMPLATE_FILES = Map.of(
    /// /            -1001601049760L, "welcome_chat_otz.html",
    /// /            -1001965242189L, "welcome_chat_predl.html",
//            -1002366865775L, "welcome_test.html"
//    );

    private Map<Long, String> templates;


}
