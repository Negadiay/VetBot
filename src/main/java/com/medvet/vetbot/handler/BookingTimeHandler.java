package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.Paginator;
import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.config.BookingProperties;
import com.medvet.vetbot.domain.BookingDraft;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.service.BookingDraftService;
import com.medvet.vetbot.service.SlotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BookingTimeHandler implements StateHandler {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final BookingProperties properties;
    private final SlotService slotService;
    private final BookingDraftService draftService;

    public BookingTimeHandler(BookingProperties properties, SlotService slotService, BookingDraftService draftService) {
        this.properties = properties;
        this.slotService = slotService;
        this.draftService = draftService;
    }

    @Override
    public BotState state() {
        return BotState.BOOKING_CHOOSING_TIME;
    }

    @Override
    public void onEnter(UserSession session) {
        showTimes(session, 0);
    }

    private void showTimes(UserSession session, int page) {
        BookingDraft draft = draftService.getOrCreate(session.getUser());
        List<LocalTime> slots = slotService.availableSlots(draft.getService().getId(), draft.getAppointmentDate());

        if (slots.isEmpty()) {
            session.sendMessage("На эту дату нет свободного времени. Выберите другую дату.", backToDateKeyboard());
            return;
        }

        List<InlineKeyboardRow> extra = List.of(
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("К выбору даты").callbackData("back:date").build())
        );

        InlineKeyboardMarkup keyboard = Paginator.paginate(
                slots,
                page,
                properties.getTimesPageSize(),
                3,
                "time_page:",
                slot -> InlineKeyboardButton.builder()
                        .text(slot.format(TIME_FORMAT))
                        .callbackData("time:" + slot)
                        .build(),
                extra
        );

        session.sendMessage("Выберите время приёма:", keyboard);
    }

    private InlineKeyboardMarkup backToDateKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("К выбору даты").callbackData("back:date").build()))
                .build();
    }

    @Override
    public void handleCallback(UserSession session, String data) {
        if (data.startsWith("time_page:")) {
            int page = Integer.parseInt(data.substring("time_page:".length()));
            showTimes(session, page);
            return;
        }

        if (data.equals("back:date")) {
            session.goTo(BotState.BOOKING_CHOOSING_DATE);
            return;
        }

        if (data.startsWith("time:")) {
            LocalTime time = LocalTime.parse(data.substring("time:".length()));
            BookingDraft draft = draftService.getOrCreate(session.getUser());
            draft.setAppointmentTime(time);
            draftService.save(draft);

            session.goTo(BotState.BOOKING_WAITING_PET_NAME);
        }
    }
}