package config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WelcomeMessageProvider {

    private static final Map<Long, String> TEMPLATE_FILES = Map.of(
            -1001601049760L, "welcome_chat_otz.html",
            -1001965242189L, "welcome_chat_predl.html",
            -1002366865775L, "welcome_test.html",
            -4762815401L, "welcome_test.html"

    );

    private static final Map<Long, String> WELCOME_TEMPLATE = preload();

    private static Map<Long, String> preload() {
        Map<Long,String> m = new HashMap<>();
        TEMPLATE_FILES.forEach((chatId, file) ->
                m.put(chatId, TemplateLoader.loadTemplate(file))
        );

        return Collections.unmodifiableMap(m);
    }

    public static Map<Long, String> getWelcomeMsg() {
        return WELCOME_TEMPLATE;
    }

}
