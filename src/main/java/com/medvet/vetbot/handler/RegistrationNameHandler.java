package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.domain.User;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class RegistrationNameHandler implements StateHandler {

    @Override
    public BotState state() {
        return BotState.REG_WAITING_NAME;
    }

    @Override
    public void handleText(UserSession session, String text) {
        String fullName = text.trim();

        if(!isValidFullName(fullName)) {
            session.sendMessage("Похоже, вы ввели некорректное ФИО. Перепроверьте введённые данные.");
            return;
        }

        User user = session.getUser();
        user.setFullName(fullName);
        session.saveUser();

        session.sendMessage("Спасибо! Нажмите кнопку ниже, чтобы заполнить номер телефона и продолжить.", buildContactKeyboard());
        session.setState(BotState.REG_WAITING_PHONE);
    }

    private boolean isValidFullName(String name) {
        if (name.length() < 5 || name.length() > 100) {
            return false;
        }
        return name.matches("[А-Яа-яЁёA-Za-z\\s-]+");
    }

    private ReplyKeyboardMarkup buildContactKeyboard() {
        KeyboardButton contactButton = KeyboardButton.builder()
                .text("Поделиться номером")
                .requestContact(true)
                .build();

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        return ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
    }
}
