package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.domain.BookingDraft;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.domain.Service;
import com.medvet.vetbot.repository.ServiceRepository;
import com.medvet.vetbot.service.BookingDraftService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingServiceHandler implements StateHandler {

    private final ServiceRepository serviceRepository;
    private final BookingDraftService draftService;

    public BookingServiceHandler(ServiceRepository serviceRepository, BookingDraftService draftService) {
        this.serviceRepository = serviceRepository;
        this.draftService = draftService;
    }

    @Override
    public BotState state() {
        return BotState.BOOKING_CHOOSING_SERVICE;
    }

    public void showServices(UserSession session) {
        List<Service> services = serviceRepository.findAll();

        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (Service service : services) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(service.getName())
                    .callbackData("service:" + service.getId())
                    .build();
            rows.add(new InlineKeyboardRow(button));
        }

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();

        session.sendMessage("Выберите услугу:", keyboard);
    }

    @Override
    public void handleCallback(UserSession session, String data) {
        if (!data.startsWith("service:")) {
            return;
        }

        Long serviceId = Long.parseLong(data.substring("service:".length()));
        Service service = serviceRepository.findById(serviceId).orElse(null);

        if (service == null) {
            session.sendMessage("Услуга не найдена. Попробуйте снова.");
            return;
        }

        BookingDraft draft = draftService.getOrCreate(session.getUser());
        draft.setService(service);
        draftService.save(draft);

        session.sendMessage("Услуга выбрана: " + service.getName() + ". Теперь выберите дату. (скоро будет)");
        session.setState(BotState.BOOKING_CHOOSING_DATE);
    }
}