package com.medvet.vetbot.service;

import com.medvet.vetbot.domain.BookingDraft;
import com.medvet.vetbot.domain.User;
import com.medvet.vetbot.repository.BookingDraftRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingDraftService {

    private final BookingDraftRepository draftRepository;

    public BookingDraftService(BookingDraftRepository draftRepository) {
        this.draftRepository = draftRepository;
    }

    public BookingDraft getOrCreate(User user) {
        return draftRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    BookingDraft draft = new BookingDraft();
                    draft.setUser(user);
                    return draftRepository.save(draft);
                });
    }

    public void save(BookingDraft draft) {
        draftRepository.save(draft);
    }

    public void clear(User user) {
        draftRepository.findByUserId(user.getId())
                .ifPresent(draftRepository::delete);
    }
}