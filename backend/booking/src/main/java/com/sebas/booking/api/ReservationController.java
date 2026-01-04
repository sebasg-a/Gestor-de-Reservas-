package com.sebas.booking.api;

import com.sebas.booking.api.dto.CreateReservationRequest;
import com.sebas.booking.domain.Reservation;
import com.sebas.booking.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation create(@Valid @RequestBody CreateReservationRequest req) {
        return reservationService.create(req);
    }
    @PatchMapping("/{id}/cancel")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void cancel(@PathVariable Long id) {
    reservationService.cancel(id);
}

}
