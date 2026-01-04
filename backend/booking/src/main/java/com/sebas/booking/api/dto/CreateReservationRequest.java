package com.sebas.booking.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateReservationRequest(
        @NotNull Long userId,
        @NotNull Long resourceId,
        @NotNull Instant startTime,
        @NotNull Instant endTime,
        String notes
) { }
