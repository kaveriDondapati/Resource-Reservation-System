package com.demo.Auth_service;

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
@AutoConfigureMockMvc
class AuthFlowTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void registerLoginAndReadProfile() throws Exception {
		String username = "user" + System.currentTimeMillis();
		String body = """
				{"username":"%s","password":"Secret@12"}
				""".formatted(username);

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.username").value(username))
				.andExpect(jsonPath("$.role").value("USER"));

		String token = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.role").value("USER"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		String jwt = token.replaceAll("(?s).*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");

		mockMvc.perform(get("/api/auth/me")
						.header("Authorization", "Bearer " + jwt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(username));

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isConflict());

		mockMvc.perform(get("/v3/api-docs/auth"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paths['/api/auth/login']").exists());

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"username":"%s","password":"Wrong@123"}
								""".formatted(username)))
				.andExpect(status().isUnauthorized());
	}
}
