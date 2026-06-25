package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.Paginator;
import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.config.BookingProperties;
import com.medvet.vetbot.domain.BookingDraft;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.service.BookingDraftService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingDateHandler implements StateHandler {

    private static final DateTimeFormatter BUTTON_FORMAT = DateTimeFormatter.ofPattern("dd.MM (EEE)");

    private final BookingProperties bookingProperties;
    private final BookingDraftService draftService;

    public BookingDateHandler(BookingProperties bookingProperties, BookingDraftService draftService) {
        this.bookingProperties = bookingProperties;
        this.draftService = draftService;
    }

    @Override
    public BotState state() {
        return BotState.BOOKING_CHOOSING_DATE;
    }

    @Override
    public void onEnter(UserSession session) {
        showDates(session, 0);
    }

    private void showDates(UserSession session, int page) {
        List<LocalDate> allDates = generateDates();

        InlineKeyboardMarkup keyboard = Paginator.paginate(
                allDates,
                page,
                bookingProperties.getDatesPageSize(),
                1,
                "date_page:",
                date -> InlineKeyboardButton.builder()
                        .text(date.format(BUTTON_FORMAT))
                        .callbackData("date:" + date)
                        .build(),
                null
        );

        session.sendMessage("Выберите дату приёма:", keyboard);
    }

    private List<LocalDate> generateDates() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < bookingProperties.getHorizonDays(); i++) {
            dates.add(today.plusDays(i));
        }
        return dates;
    }

    @Override
    public void handleCallback(UserSession session, String data) {
        if (data.startsWith("date_page:")) {
            int page = Integer.parseInt(data.substring("date_page:".length()));
            showDates(session, page);
            return;
        }

        if (data.startsWith("date:")) {
            LocalDate date = LocalDate.parse(data.substring("date:".length()));
            BookingDraft draft = draftService.getOrCreate(session.getUser());
            draft.setAppointmentDate(date);
            draftService.save(draft);

            session.goTo(BotState.BOOKING_CHOOSING_TIME);
        }
    }
}