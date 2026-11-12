package com.demo.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"spring.cloud.service-registry.auto-registration.enabled=false"
})
@AutoConfigureMockMvc
class ApiGatewayApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void healthIsServedByTheGatewayItself() throws Exception {
		mockMvc.perform(get("/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.service").value("api-gateway"));

		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/docs"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/swagger-ui.html"));

		mockMvc.perform(get("/api/docs/"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/swagger-ui.html"));
	}

}
