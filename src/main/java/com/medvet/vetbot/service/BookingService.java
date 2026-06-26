package com.medvet.vetbot.service;

import com.medvet.vetbot.domain.Booking;
import com.medvet.vetbot.domain.BookingDraft;
import com.medvet.vetbot.domain.User;
import com.medvet.vetbot.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class BookingService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final BookingRepository bookingRepository;
    private final BookingDraftService draftService;

    public BookingService(BookingRepository bookingRepository, BookingDraftService draftService) {
        this.bookingRepository = bookingRepository;
        this.draftService = draftService;
    }

    @Transactional
    public Optional<String> createFromDraft(User user) {
        BookingDraft draft = draftService.getOrCreate(user);

        boolean taken = bookingRepository.existsByServiceIdAndAppointmentDateAndAppointmentTime(
                draft.getService().getId(),
                draft.getAppointmentDate(),
                draft.getAppointmentTime());

        if (taken) {
            return Optional.empty();
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setService(draft.getService());
        booking.setAppointmentDate(draft.getAppointmentDate());
        booking.setAppointmentTime(draft.getAppointmentTime());
        booking.setPetName(draft.getPetName());
        booking.setPetType(draft.getPetType());

        Booking saved = bookingRepository.save(booking);

        String confirmation = buildConfirmation(saved);

        draftService.clear(user);

        return Optional.of(confirmation);
    }

    private String buildConfirmation(Booking booking) {
        return "Запись создана.\n\n"
                + "Услуга: " + booking.getService().getName() + "\n"
                + "Дата: " + booking.getAppointmentDate().format(DATE_FORMAT) + "\n"
                + "Время: " + booking.getAppointmentTime().format(TIME_FORMAT) + "\n"
                + "Питомец: " + booking.getPetName() + " (" + booking.getPetType().getLabel() + ")\n"
                + "Кабинет: " + booking.getService().getRoom();
    }
}