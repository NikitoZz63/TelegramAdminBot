package spring.admintgbot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import spring.admintgbot.config.RuleProperties;
import spring.admintgbot.entity.ViolatorEntity;
import spring.admintgbot.model.Violation;
import spring.admintgbot.model.ViolationType;
import spring.admintgbot.security.SecurityGuard;
import spring.admintgbot.util.LoggerToTgChat;

@Service
@RequiredArgsConstructor
public class UserManager {
    private final MessageService messageService;
    private final LoggerToTgChat logger;
    private final RuleProperties rules;
    private final ViolationService violationService;
    private final SecurityGuard securityGuard;
    private final EnforcementService enforcementService;


    public void handleViolation(long chatId, User offender, Integer messageId, Violation v) {
        if (!securityGuard.canEnforce(chatId, offender)) return;

        boolean punish = (v.type() == ViolationType.LINK)
                ? rules.getLinks().isPunish()
                : rules.getWords().isPunish();
//        if (!punish) {
//            // ничего не делаем
//            logger.log("Detected " + v.type() + " but punish=false; user=" + printable(offender));
//            return;
//        }

        if (messageId != null && rules.isDeleteOffendingMessage()) {
            messageService.delMsg(chatId, messageId);
        }

        int count = violationService.recordViolation(toViolator(offender));

        if (count == 1) {
            enforcementService.muteFor(chatId, offender.getId(), rules.getMuteSeconds());
            messageService.sendMsg(chatId, printable(offender) + " получил мут на 24 часа.");
            logger.log("🔇 MUTE 24h " + printable(offender) + " (" + v.type() + ")");
        } else {
            enforcementService.banForever(chatId, offender.getId());
            messageService.sendMsg(chatId, printable(offender) + " забанен за повторное нарушение.");
            logger.log("⛔️ BAN " + printable(offender) + " (" + v.type() + ", count=" + count + ")");
        }
    }


    private String printable(User user) {
        if (user == null) return "unknow user";
        return (user.getUserName() == null || user.getUserName().isEmpty()) ? user.getFirstName() : user.getUserName();
    }


    private ViolatorEntity toViolator(User u) {
        var e = new ViolatorEntity();
        e.setUserId(u.getId());
        e.setUserName(u.getUserName());
        e.setFirstName(u.getFirstName() == null ? "" : u.getFirstName());
        return e;
    }

}

