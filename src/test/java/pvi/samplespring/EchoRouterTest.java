package pvi.samplespring;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EchoRouterTest {

	// Spring Boot will create a `WebTestClient` for you,
	// already configure and ready to issue requests against "localhost:RANDOM_PORT"
	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void testEcho() {
		webTestClient
			// Create a GET request to test an endpoint
			.get().uri("/echo/1234567")
			.accept(MediaType.TEXT_PLAIN)
			.exchange()
			// and use the dedicated DSL to test assertions against the response
			.expectStatus().isOk()
			.expectBody(String.class).isEqualTo("ECHO: 1234567");

		webTestClient
		// Create a GET request to test an endpoint
		.get().uri("/echo/test")
		.accept(MediaType.TEXT_PLAIN)
		.exchange()
		// and use the dedicated DSL to test assertions against the response
		.expectStatus().isOk()
		.expectBody(String.class).isEqualTo("ECHO: test");

	}
}
