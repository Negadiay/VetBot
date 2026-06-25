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
    private final BookingServiceHandler bookingServiceHandler;

    public MenuHandler(ClinicProperties clinicProperties, BookingServiceHandler bookingServiceHandler) {
        this.clinicProperties = clinicProperties;
        this.bookingServiceHandler = bookingServiceHandler;
    }

    @Override
    public BotState state() {
        return BotState.MENU;
    }

    @Override
    public void handleText(UserSession session, String text) {
        showMenu(session);
    }

    @Override
    public void handleCallback(UserSession session, String data) {
        switch (data) {
            case "menu:about" -> showAbout(session);
            case "menu:booking" -> startBooking(session);
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

    private void startBooking(UserSession session) {
        session.setState(BotState.BOOKING_CHOOSING_SERVICE);
        bookingServiceHandler.showServices(session);
    }

    public void showMenu(UserSession session) {
        if (session.getUser().getRole() == Role.EMPLOYEE) {
            session.sendMessage("Меню сотрудника. Выберите действие:", buildEmployeeMenu());
        } else {
            session.sendMessage("Главное меню. Выберите действие:", buildClientMenu());
        }
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