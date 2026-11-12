package com.demo.Resource_service.Dto;

import com.demo.Resource_service.Entity.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResourceRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must be at most 120 characters")
    @Schema(example = "Board Room")
    private String name;

    @NotNull(message = "Type is required")
    @Schema(example = "MEETING_ROOM")
    private ResourceType type;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must be at most 200 characters")
    @Schema(example = "Floor 3")
    private String location;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Schema(example = "10")
    private Integer capacity;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    @Schema(example = "Large room")
    private String description;

    private Boolean active;
}
