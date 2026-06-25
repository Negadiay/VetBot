package com.medvet.vetbot.service;

import com.medvet.vetbot.config.BookingProperties;
import com.medvet.vetbot.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {

    private final BookingProperties properties;
    private final BookingRepository bookingRepository;

    public SlotService(BookingProperties properties, BookingRepository bookingRepository) {
        this.properties = properties;
        this.bookingRepository = bookingRepository;
    }

    public List<LocalTime> availableSlots(Long serviceId, LocalDate date) {
        LocalTime workStart = LocalTime.parse(properties.getWorkStart());
        LocalTime workEnd = LocalTime.parse(properties.getWorkEnd());
        int step = properties.getSlotMinutes();

        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = workStart;
        while (current.isBefore(workEnd)) {
            slots.add(current);
            current = current.plusMinutes(step);
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (date.isEqual(today)) {
            slots.removeIf(slot -> !slot.isAfter(now));
        }

        List<LocalTime> takenTimes = bookingRepository.findTakenTimes(serviceId, date);
        slots.removeAll(takenTimes);

        return slots;
    }
}