package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.Keyboards;
import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.domain.BookingDraft;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.domain.PetType;
import com.medvet.vetbot.service.BookingDraftService;
import com.medvet.vetbot.service.BookingService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookingPetTypeHandler implements StateHandler {

    private final BookingDraftService draftService;
    private final BookingService bookingService;

    public BookingPetTypeHandler(BookingDraftService draftService, BookingService bookingService) {
        this.draftService = draftService;
        this.bookingService = bookingService;
    }

    @Override
    public BotState state() {
        return BotState.BOOKING_CHOOSING_PET_TYPE;
    }

    @Override
    public void onEnter(UserSession session) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (PetType type : PetType.values()) {
            rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                    .text(type.getLabel())
                    .callbackData("pettype:" + type.name())
                    .build()));
        }
        rows.add(new InlineKeyboardRow(Keyboards.backButton()));

        session.sendMessage("Выберите вид питомца:",
                InlineKeyboardMarkup.builder().keyboard(rows).build());
    }

    @Override
    public void handleCallback(UserSession session, String data) {
        if (!data.startsWith("pettype:")) {
            return;
        }

        PetType type = PetType.valueOf(data.substring("pettype:".length()));

        BookingDraft draft = draftService.getOrCreate(session.getUser());
        draft.setPetType(type);
        draftService.save(draft);

        Optional<String> confirmation = bookingService.createFromDraft(session.getUser());

        if (confirmation.isEmpty()) {
            session.sendMessage("К сожалению, выбранное время уже заняли. Пожалуйста, выберите другое время.");
            session.goTo(BotState.BOOKING_CHOOSING_TIME);
            return;
        }

        session.sendMessage(confirmation.get());
        session.goTo(BotState.MENU);
    }
}