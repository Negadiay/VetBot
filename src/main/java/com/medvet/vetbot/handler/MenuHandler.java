package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.config.ClinicProperties;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.domain.Role;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class MenuHandler implements StateHandler {

    private final ClinicProperties clinicProperties;

    public MenuHandler(ClinicProperties clinicProperties) {
        this.clinicProperties = clinicProperties;
    }

    @Override
    public BotState state() {
        return BotState.MENU;
    }

    @Override
    public void onEnter(UserSession session) {
        if (session.getUser().getRole() == Role.EMPLOYEE) {
            session.sendMessage("Меню сотрудника. Выберите действие:", buildEmployeeMenu());
        } else {
            session.sendMessage("Главное меню. Выберите действие:", buildClientMenu());
        }
    }

    @Override
    public void handleText(UserSession session, String text) {
        onEnter(session);
    }

    @Override
    public void handleCallback(UserSession session, String data) {
        switch (data) {
            case "menu:about" -> showAbout(session);
            case "menu:booking" -> session.goTo(BotState.BOOKING_CHOOSING_SERVICE);
            case "menu:history" -> session.sendMessage("История записей — скоро.");
            case "menu:bookings" -> session.sendMessage("Записи клиники — скоро.");
            case "menu:search" -> session.sendMessage("Поиск — скоро.");
            default -> session.sendMessage("Неизвестное действие.");
        }
    }

    private void showAbout(UserSession session) {
        String text = clinicProperties.getName() + "\n"
                + clinicProperties.getAddress() + "\n"
                + "Телефон для справок: " + clinicProperties.getPhone();
        session.sendMessage(text);
    }

    private InlineKeyboardMarkup buildClientMenu() {
        InlineKeyboardButton booking = InlineKeyboardButton.builder()
                .text("Запись на приём")
                .callbackData("menu:booking")
                .build();

        InlineKeyboardButton history = InlineKeyboardButton.builder()
                .text("История")
                .callbackData("menu:history")
                .build();

        InlineKeyboardButton about = InlineKeyboardButton.builder()
                .text("О клинике")
                .callbackData("menu:about")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(booking))
                .keyboardRow(new InlineKeyboardRow(history))
                .keyboardRow(new InlineKeyboardRow(about))
                .build();
    }

    private InlineKeyboardMarkup buildEmployeeMenu() {
        InlineKeyboardButton bookings = InlineKeyboardButton.builder()
                .text("Записи")
                .callbackData("menu:bookings")
                .build();

        InlineKeyboardButton search = InlineKeyboardButton.builder()
                .text("Поиск")
                .callbackData("menu:search")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(bookings))
                .keyboardRow(new InlineKeyboardRow(search))
                .build();
    }
}