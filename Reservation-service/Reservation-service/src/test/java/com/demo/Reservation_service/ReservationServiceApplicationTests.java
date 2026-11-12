package com.demo.Reservation_service;

import com.demo.Reservation_service.Entity.Reservation;
import com.demo.Reservation_service.Entity.ReservationStatus;
import com.demo.Reservation_service.Repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:reservationdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"jwt.secret=test-secret-key-must-be-at-least-32-bytes-long",
		"resource.service.url=http://localhost:8082",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"spring.cloud.service-registry.auto-registration.enabled=false"
})
class ReservationServiceApplicationTests {

	@Autowired
	private ReservationRepository reservationRepository;

	@Test
	void overlapQueryTreatsBackToBackSlotsAsFree() {
		Reservation existing = new Reservation();
		existing.setResourceId(1L);
		existing.setResourceName("Conference Room A");
		existing.setUserId(2L);
		existing.setUsername("ada");
		existing.setStartTime(LocalDateTime.of(2026, 11, 4, 10, 0));
		existing.setEndTime(LocalDateTime.of(2026, 11, 4, 11, 0));
		existing.setStatus(ReservationStatus.CONFIRMED);
		reservationRepository.save(existing);

		boolean overlap = reservationRepository.existsByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
				1L,
				ReservationStatus.CONFIRMED,
				LocalDateTime.of(2026, 11, 4, 11, 30),
				LocalDateTime.of(2026, 11, 4, 10, 30)
		);
		boolean adjacent = reservationRepository.existsByResourceIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
				1L,
				ReservationStatus.CONFIRMED,
				LocalDateTime.of(2026, 11, 4, 12, 0),
				LocalDateTime.of(2026, 11, 4, 11, 0)
		);

		assertTrue(overlap);
		assertFalse(adjacent);
	}

}
