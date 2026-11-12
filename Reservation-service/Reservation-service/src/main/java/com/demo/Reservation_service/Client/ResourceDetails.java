package com.demo.Reservation_service.Client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDetails {
    private Long id;
    private String name;
    private boolean active;
}
