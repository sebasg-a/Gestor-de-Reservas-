package com.sebas.booking.service;

import com.sebas.booking.domain.Reservation;
import com.sebas.booking.domain.ReservationStatus;
import com.sebas.booking.repository.ReservationRepository;
import com.sebas.booking.service.availability.AvailabilityPolicy;
import com.sebas.booking.service.availability.TimeSlot;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
public class AvailabilityService {

    private final ReservationRepository reservationRepository;
    private final AvailabilityPolicy availabilityPolicy;

    public AvailabilityService(ReservationRepository reservationRepository,
                               AvailabilityPolicy availabilityPolicy) {
        this.reservationRepository = reservationRepository;
        this.availabilityPolicy = availabilityPolicy;
    }

    public List<TimeSlot> availabilityForDay(Long resourceId, LocalDate date) {
        Instant dayStart = date.atTime(8, 0).toInstant(ZoneOffset.UTC);
        Instant dayEnd = date.atTime(18, 0).toInstant(ZoneOffset.UTC);

        List<Reservation> existing = reservationRepository.findActiveForDay(
                resourceId,
                ReservationStatus.ACTIVE,
                dayStart,
                dayEnd
        );

        return availabilityPolicy.availableSlots(dayStart, dayEnd, existing);
    }
}
