package handlers;

import java.util.Map;

public class WelcomeMessageProvider {

    private static final String WELCOME_MSG_CHAT_OTZ = "🔥 <b>Добро пожаловать, {username} !</b> 🔥\n" +
            "Здесь вы можете оставлять отзывы, делиться информацией по ассортименту и пожеланиями.\n\n" +
            "🔹 <b>Вопросы по наличию, доставке, фото и т. п.</b> — вся информация есть на нашем Telegram-канале.\n" +
            "🔹 <b>Сделать заказ, уточнить условия, посмотреть ПРАЙС и любые вопросы</b> — пишите в ЛС: @Smoke_OPTOMMSK.\n\n" +
            "🚫 <b>Запрещено:</b>\n" +
            "— Скидывать наши прайсы и ссылки на канал.\n" +
            "— Оскорбления, реклама, ссылки на другие табачные магазины и ресурсы.\n" +
            "— Обсуждение тем, не относящихся к табаку.\n" +
            "❗ <b>Нарушение правил — БАН.</b>\n\n" +
            "💰 <b>Оплата доставки:</b>\n" +
            "После отправки мы пришлем ТТН для отслеживания и реквизиты.\n" +
            "Вы оплачиваете <b>сумму в ТТН минус 10 000 руб.</b>\n" +
            "Через 1–2 дня сумма обнуляется, и вы забираете посылку без доплаты.\n\n" +
            "Просим отнестись с пониманием! ✅";

    private static final String WELCOME_MSG_CHAT_PREDL = "🔥 <b>Добро пожаловать, {username} !</b> 🔥\n" +
            "Этот чат создан для пожеланий и предложений по наличию сигарет, которых у нас нет.\n\n" +
            "🔹 Вы можете скидывать как фото, так и названия сигарет.\n" +
            "🔹 <b>Чем больше повторений названия сигарет, тем больше вероятности появления позиции у нас.</b>\n" +
            "🔹 <b>Сделать заказ, уточнить условия, посмотреть ПРАЙС и любые вопросы</b> — пишите в ЛС: @Smoke_OPTOMMSK.\n\n" +
            "🚫 <b>ЛЮБОЕ ДРУГОЕ ОБСУЖДЕНИЕ В ЭТОМ ЧАТЕ ЗАПРЕЩЕННО. ЗА НАРУШЕНИЕ БАН!</b> 🚫\n";

    private static final String WELCOME_MSG_FOR_TEST = " <b>Добро пожаловать, {username} !</b>";

    private static final Map<Long, String> WELCOM_MSG = Map.of(
            -1001601049760L, WELCOME_MSG_CHAT_OTZ,
            -1001965242189L, WELCOME_MSG_CHAT_PREDL,
            -1002366865775L, WELCOME_MSG_FOR_TEST,
            -4762815401L, WELCOME_MSG_FOR_TEST

    );

    public static Map<Long, String> getWelcomMsg() {
        return WELCOM_MSG;
    }

}
