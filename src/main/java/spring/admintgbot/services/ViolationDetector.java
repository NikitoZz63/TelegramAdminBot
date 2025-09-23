package spring.admintgbot.services;

import com.github.demidko.aot.WordformMeaning;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import spring.admintgbot.config.RuleProperties;
import spring.admintgbot.model.Violation;
import spring.admintgbot.model.ViolationType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

@Service
@RequiredArgsConstructor
public class ViolationDetector {

    private static final Pattern SPLIT_WS = Pattern.compile("\\s+");
    private final RuleProperties rules;
    private final AtomicReference<Set<String>> badWordsRef = new AtomicReference<>();

    public Optional<Violation> find(Message msg) {
        if (msg == null) return Optional.empty();

        if (rules.getLinks().isPunish()) {
            if (hasLinkByEntities(msg)) {
                return Optional.of(new Violation(ViolationType.LINK, "link: " + msg));
            }
        }

        if (rules.getWords().isPunish()) {
            String text = getAnyText(msg);
            if (text != null && !text.isBlank()) {
                ensureDictionaryLoaded();
                String normalized = rules.getWords().isNormalize() ? normalize(text) : text.toLowerCase();
                String badLemma = findBadLemma(normalized);
                if (badLemma != null) {
                    return Optional.of(new Violation(ViolationType.WORD, "bad-word:" + badLemma));
                }
            }
        }
        return Optional.empty();
    }


    private boolean hasLinkByEntities(Message msg) {
        if (hasLinkEntities(msg.getEntities())) return true;
        return hasLinkEntities(msg.getCaptionEntities());

    }

    private boolean hasLinkEntities(List<MessageEntity> entities) {
        if (entities == null || entities.isEmpty()) return false;
        for (MessageEntity entity : entities) {
            String type = entity.getType();
            if ("url".equals(type) || "text_link".equals(type)) {
                return true;
            }
        }
        return false;
    }

    private String findBadLemma(String normalized) {
        Set<String> badWords = badWordsRef.get();

        if (badWords == null || badWords.isEmpty()) return null;

        if (!normalized.matches(".*[а-яё].*")) return null;

        for (String token : SPLIT_WS.split(normalized)) {
            if (token.isBlank()) continue;
            if (token.length() < 2) continue;

            var meanings = lookupForMeanings(token);

            if (meanings == null || meanings.isEmpty()) continue;

            for (WordformMeaning meaning : meanings) {
                String lemma = meaning.getLemma().toString().toLowerCase();
                if (badWords.contains(lemma)) {
                    return lemma;
                }
            }
        }

        return null;
    }

    private String normalize(String s) {
        if (s == null) return null;
        // латиница → похожие кириллические
        s = s
                .replace('x', 'х').replace('y', 'у').replace('e', 'е').replace('o', 'о')
                .replace('a', 'а').replace('c', 'с').replace('p', 'р')
                .replace('X', 'Х').replace('Y', 'У').replace('E', 'Е').replace('O', 'О')
                .replace('A', 'А').replace('C', 'С').replace('P', 'Р');

        s = s.toLowerCase();
        // оставляем кириллицу и пробелы
        s = s.replaceAll("[^а-яё\\s]", " ");
        return s;
    }


    private void ensureDictionaryLoaded() {
        if (badWordsRef.get() != null) return; // уже загружен
        synchronized (badWordsRef) {
            if (badWordsRef.get() != null) return;

            String src = rules.getWords().getSource();
            String path = src.startsWith("classpath:") ? src.substring("classpath:".length()) : src;

            try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    badWordsRef.set(Set.of()); // нет файла — пустой словарь
                    return;
                }
                try (var br = new BufferedReader(new InputStreamReader(is))) {
                    Set<String> dict = br.lines()
                            .map(String::trim)
                            .filter(s -> !s.isEmpty() && !s.startsWith("#"))
                            .map(String::toLowerCase)
                            .collect(Collectors.toUnmodifiableSet());
                    badWordsRef.set(dict);
                }
            } catch (Exception e) {
                badWordsRef.set(Set.of()); // при ошибке — пустой словарь
            }
        }
    }


    private String getAnyText(Message msg) {
        if (msg.getText() != null) return msg.getText();
        return msg.getCaption();
    }

}
