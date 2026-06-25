package com.medvet.vetbot.repository;

import com.medvet.vetbot.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
