package com.demo.Resource_service.Service;

import com.demo.Resource_service.Dto.ResourceRequest;
import com.demo.Resource_service.Dto.ResourceResponse;
import com.demo.Resource_service.Entity.BookableResource;
import com.demo.Resource_service.Repository.ResourceRepository;
import com.demo.Resource_service.Security.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceCatalogService {

    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<ResourceResponse> list(boolean includeInactive) {
        if (includeInactive && !isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can include inactive resources");
        }
        List<BookableResource> resources = includeInactive
                ? resourceRepository.findAllByOrderByNameAsc()
                : resourceRepository.findByActiveTrueOrderByNameAsc();
        return resources.stream().map(ResourceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ResourceResponse get(Long id) {
        return ResourceResponse.from(find(id));
    }

    @Transactional
    public ResourceResponse create(ResourceRequest request) {
        BookableResource resource = new BookableResource();
        apply(resource, request);
        resource.setActive(true);
        return ResourceResponse.from(resourceRepository.save(resource));
    }

    @Transactional
    public ResourceResponse update(Long id, ResourceRequest request) {
        BookableResource resource = find(id);
        apply(resource, request);
        if (request.getActive() != null) {
            resource.setActive(request.getActive());
        }
        return ResourceResponse.from(resourceRepository.save(resource));
    }

    @Transactional
    public void deactivate(Long id) {
        BookableResource resource = find(id);
        resource.setActive(false);
        resourceRepository.save(resource);
    }

    private BookableResource find(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
    }

    private void apply(BookableResource resource, ResourceRequest request) {
        resource.setName(request.getName().trim());
        resource.setType(request.getType());
        resource.setLocation(request.getLocation().trim());
        resource.setCapacity(request.getCapacity());
        resource.setDescription(request.getDescription());
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getPrincipal() instanceof JwtPrincipal principal
                && "ADMIN".equals(principal.role());
    }
}
