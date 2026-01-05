package com.sebas.booking.service.availability;

import com.sebas.booking.domain.Reservation;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class HourlyAvailabilityPolicy implements AvailabilityPolicy {

    private static final Duration SLOT = Duration.ofHours(1);

    @Override
public List<TimeSlot> availableSlots(Instant dayStart, Instant dayEnd, List<Reservation> existing) {
    List<TimeSlot> slots = new ArrayList<>();

    for (Instant cursor = dayStart;
         cursor.plus(SLOT).compareTo(dayEnd) <= 0;
         cursor = cursor.plus(SLOT)) {

        final Instant slotStart = cursor;
        final Instant slotEnd = cursor.plus(SLOT);

        boolean overlaps = existing.stream().anyMatch(r ->
                r.getStartTime().isBefore(slotEnd)
                        && r.getEndTime().isAfter(slotStart)
        );

        if (!overlaps) {
            slots.add(new TimeSlot(slotStart, slotEnd));
        }
    }

    return slots;
}

}
