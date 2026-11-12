package com.demo.Reservation_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private Long resourceId;
    private String resourceName;
    private Long userId;
    private String username;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String purpose;
    private LocalDateTime createdAt;
}
