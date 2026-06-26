package com.medvet.vetbot.repository;

import com.medvet.vetbot.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b.appointmentTime FROM Booking b WHERE b.service.id = :serviceId AND b.appointmentDate = :date")
    List<LocalTime> findTakenTimes(@Param("serviceId") Long serviceId, @Param("date") LocalDate date);

    boolean existsByServiceIdAndAppointmentDateAndAppointmentTime(Long serviceId, LocalDate date, LocalTime time);
}
