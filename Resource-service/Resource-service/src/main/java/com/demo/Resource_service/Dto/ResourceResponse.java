package com.demo.Resource_service.Dto;

import com.demo.Resource_service.Entity.BookableResource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResourceResponse {
    private Long id;
    private String name;
    private String type;
    private String location;
    private Integer capacity;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    public static ResourceResponse from(BookableResource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getType().name(),
                resource.getLocation(),
                resource.getCapacity(),
                resource.getDescription(),
                resource.isActive(),
                resource.getCreatedAt()
        );
    }
}
