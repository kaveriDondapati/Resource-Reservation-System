package com.demo.Auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:authdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"jwt.secret=test-secret-key-must-be-at-least-32-bytes-long",
		"jwt.expiration=3600000",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"spring.cloud.service-registry.auto-registration.enabled=false"
})
class AuthServiceApplicationTests {

	@Autowired
	private Environment environment;

	@Test
	void contextLoads() {
		assertEquals("postgres", environment.getProperty("DB_USERNAME"));
	}

}
