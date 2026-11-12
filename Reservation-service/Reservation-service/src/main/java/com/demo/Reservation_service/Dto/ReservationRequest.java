package com.demo.Reservation_service.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {

    @NotNull(message = "Resource id is required")
    @Schema(example = "1")
    private Long resourceId;

    @NotNull(message = "Start time is required")
    @Schema(example = "2026-11-04T10:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Schema(example = "2026-11-04T11:00:00")
    private LocalDateTime endTime;

    @Size(max = 500, message = "Purpose must be at most 500 characters")
    @Schema(example = "Sprint planning")
    private String purpose;
}
