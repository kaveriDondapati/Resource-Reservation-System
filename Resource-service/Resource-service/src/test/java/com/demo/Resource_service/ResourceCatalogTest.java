package com.demo.Resource_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
@AutoConfigureMockMvc
class ResourceCatalogTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void catalogIsPublicAndWritesRequireAuthentication() throws Exception {
		mockMvc.perform(get("/v3/api-docs/resources"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paths['/api/resources']").exists());

		mockMvc.perform(get("/api/resources"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").isNotEmpty());

		mockMvc.perform(post("/api/resources")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name":"Board Room","type":"MEETING_ROOM","location":"Floor 3","capacity":10}
								"""))
				.andExpect(status().isUnauthorized());
	}
}
