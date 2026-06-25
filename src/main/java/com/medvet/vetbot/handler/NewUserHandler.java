package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.domain.BotState;
import org.springframework.stereotype.Component;

@Component
public class NewUserHandler implements StateHandler {

    @Override
    public BotState state() {
        return BotState.NEW;
    }

    @Override
    public void handleText(UserSession session, String text) {
        session.sendMessage("Добро пожаловать в MEDVET Bot! Для продолжения нужно пройти регистрацию. Введите ваше ФИО:");
        session.setState(BotState.REG_WAITING_NAME);
    }
}