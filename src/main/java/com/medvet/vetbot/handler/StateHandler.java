package com.medvet.vetbot.handler;

import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.bot.UserSession;

public interface StateHandler {
    BotState state();

    default void handleText(UserSession session, String text) {
        session.sendMessage("Не распознал запрос. Воспользуйтесь кнопками для навигации.");
    }

    default void handleCallback(UserSession session, String data) {
        session.sendMessage("Это действие сейчас недоступно. Попробуйте снова.");
    }
}
