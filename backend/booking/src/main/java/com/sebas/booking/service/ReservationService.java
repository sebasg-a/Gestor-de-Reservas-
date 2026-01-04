package com.sebas.booking.service;

import com.sebas.booking.api.dto.CreateReservationRequest;
import com.sebas.booking.domain.*;
import com.sebas.booking.repository.ReservationRepository;
import com.sebas.booking.repository.ResourceRepository;
import com.sebas.booking.repository.UserRepository;
import com.sebas.booking.service.exception.ConflictException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository,
                              ResourceRepository resourceRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }
    @Transactional
public void cancel(Long reservationId) {
    Reservation r = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

    if (r.getStatus() == ReservationStatus.CANCELLED) return;

    r.cancel();
    reservationRepository.save(r);
}


    @Transactional
    public Reservation create(CreateReservationRequest req) {
        validateTimes(req.startTime(), req.endTime());

        boolean overlap = reservationRepository.existsOverlap(
                req.resourceId(),
                ReservationStatus.ACTIVE,
                req.startTime(),
                req.endTime()
        );

        if (overlap) {
            throw new ConflictException("Resource already reserved for that time range");
        }

        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Resource resource = resourceRepository.findById(req.resourceId())
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        Reservation reservation = Reservation.builder()
                .user(user)
                .resource(resource)
                .startTime(req.startTime())
                .endTime(req.endTime())
                .status(ReservationStatus.ACTIVE)
                .notes(req.notes())
                .build();

        return reservationRepository.save(reservation);
    }

    private void validateTimes(Instant start, Instant end) {
        if (start == null || end == null) throw new IllegalArgumentException("Start and end are required");
        if (!end.isAfter(start)) throw new IllegalArgumentException("endTime must be after startTime");
    }
}
