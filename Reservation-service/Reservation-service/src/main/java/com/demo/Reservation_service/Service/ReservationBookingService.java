package com.demo.Reservation_service.Service;

import com.demo.Reservation_service.Client.ResourceClient;
import com.demo.Reservation_service.Client.ResourceDetails;
import com.demo.Reservation_service.Dto.ReservationRequest;
import com.demo.Reservation_service.Dto.ReservationResponse;
import com.demo.Reservation_service.Entity.Reservation;
import com.demo.Reservation_service.Entity.ReservationStatus;
import com.demo.Reservation_service.Repository.ReservationRepository;
import com.demo.Reservation_service.Security.JwtPrincipal;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationBookingService {

    private static final Logger log = LoggerFactory.getLogger(ReservationBookingService.class);
    private static final Duration MAX_LENGTH = Duration.ofHours(24);

    private final ReservationRepository reservationRepository;
    private final ResourceClient resourceClient;
    private final CurrentUser currentUser;

    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        JwtPrincipal principal = currentUser.require();
        validateWindow(request.getStartTime(), request.getEndTime());
        ResourceDetails resource = loadResource(request.getResourceId());
        if (!resource.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Resource is not available");
        }
        boolean overlap = reservationRepository
                .existsByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        resource.getId(),
                        ReservationStatus.CONFIRMED,
                        request.getEndTime(),
                        request.getStartTime()
                );
        if (overlap) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Resource is already booked for that time");
        }

        Reservation reservation = new Reservation();
        reservation.setResourceId(resource.getId());
        reservation.setResourceName(resource.getName() == null ? "Resource " + resource.getId() : resource.getName());
        reservation.setUserId(principal.userId());
        reservation.setUsername(principal.username());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setPurpose(request.getPurpose());
        return toResponse(reservationRepository.save(reservation), true);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> mine() {
        JwtPrincipal principal = currentUser.require();
        return reservationRepository.findByUsernameOrderByStartTimeDesc(principal.username()).stream()
                .map(reservation -> toResponse(reservation, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> all() {
        JwtPrincipal principal = currentUser.require();
        requireAdmin(principal);
        return reservationRepository.findAllByOrderByStartTimeDesc().stream()
                .map(reservation -> toResponse(reservation, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> forResource(Long resourceId, LocalDateTime from, LocalDateTime to) {
        JwtPrincipal principal = currentUser.require();
        return reservationRepository
                .findByResourceIdAndStatusOrderByStartTimeAsc(resourceId, ReservationStatus.CONFIRMED)
                .stream()
                .filter(reservation -> from == null || reservation.getEndTime().isAfter(from))
                .filter(reservation -> to == null || reservation.getStartTime().isBefore(to))
                .map(reservation -> toResponse(reservation, canSeeDetails(principal, reservation)))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse get(Long id) {
        JwtPrincipal principal = currentUser.require();
        Reservation reservation = find(id);
        if (!canSeeDetails(principal, reservation)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own reservations");
        }
        return toResponse(reservation, true);
    }

    @Transactional
    public ReservationResponse cancel(Long id) {
        JwtPrincipal principal = currentUser.require();
        Reservation reservation = find(id);
        if (!canSeeDetails(principal, reservation)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own reservations");
        }
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation is already cancelled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return toResponse(reservationRepository.save(reservation), true);
    }

    private void validateWindow(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time");
        }
        if (start.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time cannot be in the past");
        }
        if (Duration.between(start, end).compareTo(MAX_LENGTH) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation cannot be longer than 24 hours");
        }
    }

    private ResourceDetails loadResource(Long resourceId) {
        try {
            ResourceDetails resource = resourceClient.getById(resourceId);
            if (resource == null || resource.getId() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
            }
            return resource;
        } catch (FeignException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } catch (FeignException ex) {
            log.warn("Resource service call failed with status {}", ex.status());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Resource service is unavailable");
        }
    }

    private Reservation find(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
    }

    private void requireAdmin(JwtPrincipal principal) {
        if (!"ADMIN".equals(principal.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role is required");
        }
    }

    private boolean canSeeDetails(JwtPrincipal principal, Reservation reservation) {
        return "ADMIN".equals(principal.role()) || principal.username().equals(reservation.getUsername());
    }

    private ReservationResponse toResponse(Reservation reservation, boolean includePrivateDetails) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getResourceId(),
                reservation.getResourceName(),
                includePrivateDetails ? reservation.getUserId() : null,
                includePrivateDetails ? reservation.getUsername() : null,
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus().name(),
                includePrivateDetails ? reservation.getPurpose() : null,
                includePrivateDetails ? reservation.getCreatedAt() : null
        );
    }
}
