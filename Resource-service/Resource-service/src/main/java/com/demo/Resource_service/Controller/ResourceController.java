package com.demo.Resource_service.Controller;

import com.demo.Resource_service.Dto.ResourceRequest;
import com.demo.Resource_service.Dto.ResourceResponse;
import com.demo.Resource_service.Service.ResourceCatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resources")
public class ResourceController {

    private final ResourceCatalogService resourceCatalogService;

    @GetMapping
    public List<ResourceResponse> list(@RequestParam(defaultValue = "false") boolean includeInactive) {
        return resourceCatalogService.list(includeInactive);
    }

    @GetMapping("/{id}")
    public ResourceResponse get(@PathVariable Long id) {
        return resourceCatalogService.get(id);
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceCatalogService.create(request));
    }

    @PutMapping("/{id}")
    public ResourceResponse update(@PathVariable Long id, @Valid @RequestBody ResourceRequest request) {
        return resourceCatalogService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        resourceCatalogService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
