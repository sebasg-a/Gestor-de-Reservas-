package com.sebas.booking.repository;

import com.sebas.booking.domain.Reservation;
import com.sebas.booking.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select (count(r) > 0) from Reservation r
            where r.resource.id = :resourceId
              and r.status = :status
              and r.startTime < :newEnd
              and r.endTime > :newStart
            """)
    boolean existsOverlap(@Param("resourceId") Long resourceId,
                          @Param("status") ReservationStatus status,
                          @Param("newStart") Instant newStart,
                          @Param("newEnd") Instant newEnd);
}
