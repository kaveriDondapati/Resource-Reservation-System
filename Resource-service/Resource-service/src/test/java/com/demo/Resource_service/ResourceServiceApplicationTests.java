package com.demo.Resource_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:resourcedb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"jwt.secret=test-secret-key-must-be-at-least-32-bytes-long",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"spring.cloud.service-registry.auto-registration.enabled=false",
		"app.seed-resources=true"
})
class ResourceServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
