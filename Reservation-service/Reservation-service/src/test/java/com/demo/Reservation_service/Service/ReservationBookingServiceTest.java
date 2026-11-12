package com.demo.Reservation_service.Service;

import com.demo.Reservation_service.Client.ResourceClient;
import com.demo.Reservation_service.Client.ResourceDetails;
import com.demo.Reservation_service.Dto.ReservationRequest;
import com.demo.Reservation_service.Repository.ReservationRepository;
import com.demo.Reservation_service.Security.JwtPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationBookingServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceClient resourceClient;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ReservationBookingService reservationBookingService;

    @Test
    void rejectsASecondBookingThatOverlaps() {
        when(currentUser.require()).thenReturn(new JwtPrincipal(1L, "ada", "USER"));
        when(resourceClient.getById(5L)).thenReturn(new ResourceDetails(5L, "Conference Room A", true));
        when(reservationRepository.existsByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                eq(5L), any(), any(), any())).thenReturn(true);

        ReservationRequest request = request(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reservationBookingService.create(request));

        assertEquals(409, ex.getStatusCode().value());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void rejectsAnInactiveResource() {
        when(currentUser.require()).thenReturn(new JwtPrincipal(1L, "ada", "USER"));
        when(resourceClient.getById(5L)).thenReturn(new ResourceDetails(5L, "Conference Room A", false));

        ReservationRequest request = request(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reservationBookingService.create(request));

        assertEquals(409, ex.getStatusCode().value());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void rejectsAStartTimeInThePast() {
        when(currentUser.require()).thenReturn(new JwtPrincipal(1L, "ada", "USER"));
        ReservationRequest request = request(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reservationBookingService.create(request));

        assertEquals(400, ex.getStatusCode().value());
        verify(resourceClient, never()).getById(any());
    }

    private ReservationRequest request(LocalDateTime start, LocalDateTime end) {
        ReservationRequest request = new ReservationRequest();
        request.setResourceId(5L);
        request.setStartTime(start);
        request.setEndTime(end);
        request.setPurpose("Sprint planning");
        return request;
    }
}
