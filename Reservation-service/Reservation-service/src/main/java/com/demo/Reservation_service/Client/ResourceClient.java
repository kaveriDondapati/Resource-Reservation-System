package com.demo.Reservation_service.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "resource-service", url = "${resource.service.url}")
public interface ResourceClient {

    @GetMapping("/api/resources/{id}")
    ResourceDetails getById(@PathVariable("id") Long id);
}
