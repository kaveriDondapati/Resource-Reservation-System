package com.demo.Reservation_service.Repository;

import com.demo.Reservation_service.Entity.Reservation;
import com.demo.Reservation_service.Entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long resourceId,
            ReservationStatus status,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Reservation> findByUsernameOrderByStartTimeDesc(String username);

    List<Reservation> findAllByOrderByStartTimeDesc();

    List<Reservation> findByResourceIdAndStatusOrderByStartTimeAsc(Long resourceId, ReservationStatus status);
}
