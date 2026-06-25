package com.medvet.vetbot.repository;

import com.medvet.vetbot.domain.BookingDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingDraftRepository extends JpaRepository<BookingDraft, Long> {

    Optional<BookingDraft> findByUserId(Long userId);
}
