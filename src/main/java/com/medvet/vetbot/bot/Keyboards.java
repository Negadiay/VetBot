package com.medvet.vetbot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public final class Keyboards {

    public static final String MENU_BUTTON_TEXT = "Меню";

    private Keyboards() {
    }

    public static ReplyKeyboardMarkup menuButton() {
        KeyboardButton button = KeyboardButton.builder()
                .text(MENU_BUTTON_TEXT)
                .build();

        KeyboardRow row = new KeyboardRow();
        row.add(button);

        return ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .resizeKeyboard(true)
                .build();
    }

    public static InlineKeyboardButton backButton() {
        return InlineKeyboardButton.builder()
                .text("Пред. этап")
                .callbackData(BackNavigation.BACK_CALLBACK)
                .build();
    }
}