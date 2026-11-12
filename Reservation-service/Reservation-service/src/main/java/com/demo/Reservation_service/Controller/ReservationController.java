package com.demo.Reservation_service.Controller;

import com.demo.Reservation_service.Dto.ReservationRequest;
import com.demo.Reservation_service.Dto.ReservationResponse;
import com.demo.Reservation_service.Service.ReservationBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations")
public class ReservationController {

    private final ReservationBookingService reservationBookingService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationBookingService.create(request));
    }

    @GetMapping("/me")
    public List<ReservationResponse> mine() {
        return reservationBookingService.mine();
    }

    @GetMapping
    public List<ReservationResponse> all() {
        return reservationBookingService.all();
    }

    @GetMapping("/resource/{resourceId}")
    public List<ReservationResponse> forResource(
            @PathVariable Long resourceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return reservationBookingService.forResource(resourceId, from, to);
    }

    @GetMapping("/{id}")
    public ReservationResponse get(@PathVariable Long id) {
        return reservationBookingService.get(id);
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id) {
        return reservationBookingService.cancel(id);
    }
}
