package com.demo.Resource_service.Config;

import com.demo.Resource_service.Entity.BookableResource;
import com.demo.Resource_service.Entity.ResourceType;
import com.demo.Resource_service.Repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceSeeder implements CommandLineRunner {

    private final ResourceRepository resourceRepository;

    @Value("${app.seed-resources:true}")
    private boolean seedResources;

    @Override
    public void run(String... args) {
        if (!seedResources) {
            return;
        }
        save("Conference Room A", ResourceType.MEETING_ROOM, "Floor 1", 8, "Projector and whiteboard");
        save("Board Room", ResourceType.MEETING_ROOM, "Floor 3", 12, "Large table with a video call screen");
        save("Huddle Room", ResourceType.MEETING_ROOM, "Floor 1", 4, "Small room for a quick meeting");
        save("Training Room", ResourceType.MEETING_ROOM, "Floor 4", 20, "Classroom layout with a whiteboard wall");
        save("Focus Desk 1", ResourceType.DESK, "Floor 2", 1, "Quiet desk by the window");
        save("Focus Desk 2", ResourceType.DESK, "Floor 2", 1, "Standing desk near the plants");
        save("Hot Desk 3", ResourceType.DESK, "Floor 2", 1, "Shared desk by the kitchen");
        save("Quiet Desk 4", ResourceType.DESK, "Floor 5", 1, "Away from the hallway");
        save("Projector Kit", ResourceType.EQUIPMENT, "Storage", 1, "Portable projector and screen");
        save("Laptop Cart", ResourceType.EQUIPMENT, "IT cage", 8, "Loaner laptops for a workshop");
        save("Camera Kit", ResourceType.EQUIPMENT, "Studio", 1, "Tripod and conference camera");
        save("Company Van", ResourceType.VEHICLE, "Parking B", 4, "City travel");
        save("Pool Car", ResourceType.VEHICLE, "Parking A", 5, "Day trips out of the office");
        save("Cargo Bike", ResourceType.VEHICLE, "Bike rack", 1, "Short deliveries nearby");
    }

    private void save(String name, ResourceType type, String location, int capacity, String description) {
        if (resourceRepository.existsByName(name)) {
            return;
        }
        BookableResource resource = new BookableResource();
        resource.setName(name);
        resource.setType(type);
        resource.setLocation(location);
        resource.setCapacity(capacity);
        resource.setDescription(description);
        resource.setActive(true);
        resourceRepository.save(resource);
    }
}
