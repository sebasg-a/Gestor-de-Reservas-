package com.sebas.booking.service;

import com.sebas.booking.api.dto.CreateReservationRequest;
import com.sebas.booking.domain.ReservationStatus;
import com.sebas.booking.repository.ReservationRepository;
import com.sebas.booking.repository.ResourceRepository;
import com.sebas.booking.repository.UserRepository;
import com.sebas.booking.service.exception.ConflictException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Test
    void shouldThrowConflictWhenOverlapExists() {
        ReservationRepository reservationRepository = mock(ReservationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ResourceRepository resourceRepository = mock(ResourceRepository.class);

        ReservationService service = new ReservationService(reservationRepository, userRepository, resourceRepository);

        Instant start = Instant.parse("2026-01-05T13:30:00Z");
        Instant end = Instant.parse("2026-01-05T14:30:00Z");

        when(reservationRepository.existsOverlap(1L, ReservationStatus.ACTIVE, start, end))
                .thenReturn(true);

        CreateReservationRequest req = new CreateReservationRequest(1L, 1L, start, end, "solape");

        assertThrows(ConflictException.class, () -> service.create(req));

        verify(userRepository, never()).findById(anyLong());
        verify(resourceRepository, never()).findById(anyLong());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void shouldRejectEndBeforeStart() {
        ReservationRepository reservationRepository = mock(ReservationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ResourceRepository resourceRepository = mock(ResourceRepository.class);

        ReservationService service = new ReservationService(reservationRepository, userRepository, resourceRepository);

        Instant start = Instant.parse("2026-01-05T14:00:00Z");
        Instant end = Instant.parse("2026-01-05T13:00:00Z");

        CreateReservationRequest req = new CreateReservationRequest(1L, 1L, start, end, "bad");

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }
}
