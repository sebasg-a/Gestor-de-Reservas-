package com.sebas.booking.service.availability;

import java.time.Instant;

public record TimeSlot(Instant startTime, Instant endTime) { }
