package spring.admintgbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rules")
public class RuleProperties {
    private int muteSeconds = 86400;
    private boolean deleteOffendingMessage = true;
    private Links links = new Links();
    private Words words = new Words();

    @Getter
    @Setter
    public static class Links {
        private boolean punish = true;
    }

    @Getter
    @Setter
    public static class Words {
        private boolean punish = false;
        private String source = "classpath:forbiddenWords.txt";
        private boolean normalize = true;
    }

}
