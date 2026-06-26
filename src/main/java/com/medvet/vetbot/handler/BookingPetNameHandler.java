package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.Keyboards;
import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.domain.BookingDraft;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.service.BookingDraftService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class BookingPetNameHandler implements StateHandler {

    private final BookingDraftService draftService;

    public BookingPetNameHandler(BookingDraftService draftService) {
        this.draftService = draftService;
    }

    @Override
    public BotState state() {
        return BotState.BOOKING_WAITING_PET_NAME;
    }

    @Override
    public void onEnter(UserSession session) {
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(new InlineKeyboardRow(Keyboards.backButton()))
                .build();
        session.sendMessage("Введите кличку питомца:", keyboard);
    }

    @Override
    public void handleText(UserSession session, String text) {
        String petName = text.trim();

        if (petName.isEmpty() || petName.length() > 50) {
            session.sendMessage("Кличка должна быть от 1 до 50 символов. Введите ещё раз:");
            return;
        }

        BookingDraft draft = draftService.getOrCreate(session.getUser());
        draft.setPetName(petName);
        draftService.save(draft);

        session.goTo(BotState.BOOKING_CHOOSING_PET_TYPE);
    }
}