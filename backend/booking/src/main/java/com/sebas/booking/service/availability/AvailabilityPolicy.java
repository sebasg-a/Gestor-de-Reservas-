package com.sebas.booking.service.availability;

import com.sebas.booking.domain.Reservation;

import java.time.Instant;
import java.util.List;

public interface AvailabilityPolicy {
    List<TimeSlot> availableSlots(Instant dayStart, Instant dayEnd, List<Reservation> existing);
}
