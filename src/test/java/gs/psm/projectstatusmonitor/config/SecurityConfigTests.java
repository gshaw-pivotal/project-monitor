package gs.psm.projectstatusmonitor.config;

import gs.psm.projectstatusmonitor.config.SecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SecurityConfig.class)
public class SecurityConfigTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void GET_requestAListOfAllProjects_withoutValidCredentials_resultsInUnauthorized() {
		ResponseEntity<List> response = restTemplate
				.withBasicAuth("", "")
				.getForEntity("http://localhost:" + serverPort + "/project/list", List.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void GET_requestAListOfAllProjects_withValidCredentials_resultsInOk() {
		ResponseEntity<List> response = restTemplate
				.withBasicAuth("test_username", "test_password")
				.getForEntity("http://localhost:" + serverPort + "/project/list", List.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void GET_requestAProject_withoutValidCredentials_resultsInUnauthorized() {
		ResponseEntity<List> response = restTemplate
				.withBasicAuth("", "")
				.getForEntity("http://localhost:" + serverPort + "/project/123", List.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void GET_requestAProject_thatDoesNotExist_withValidCredentials_resultsInBadRequest() {
		ResponseEntity<List> response = restTemplate
				.withBasicAuth("test_username", "test_password")
				.getForEntity("http://localhost:" + serverPort + "/project/123", List.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void DELETE_deleteAProject_withoutValidCredentials_resultsInUnauthorized() {
		ResponseEntity response = restTemplate
				.withBasicAuth("", "")
				.exchange("http://localhost:" + serverPort + "/project/123", HttpMethod.DELETE, null, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void DELETE_deleteAProject_thatDoesNotExist_withValidCredentials_resultsInBadRequest() {
		ResponseEntity response = restTemplate
				.withBasicAuth("test_username", "test_password")
				.exchange("http://localhost:" + serverPort + "/project/123", HttpMethod.DELETE, null, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void POST_addAProject_withoutValidCredentials_resultsInUnauthorized() {
		ResponseEntity response = restTemplate
				.withBasicAuth("", "")
				.postForEntity("http://localhost:" + serverPort + "/project/add", "", Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void POST_addAProject__withEmptyBody_withValidCredentials_resultsInUnsupportedMediaType() {
		ResponseEntity response = restTemplate
				.withBasicAuth("test_username", "test_password")
				.postForEntity("http://localhost:" + serverPort + "/project/add", "{}", Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@Test
	public void POST_updateAProject_withoutValidCredentials_resultsInUnauthorized() {
		ResponseEntity response = restTemplate
				.withBasicAuth("", "")
				.postForEntity("http://localhost:" + serverPort + "/project/update", "", Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void POST_updateAProject__withEmptyBody_withValidCredentials_resultsInUnsupportedMediaType() {
		ResponseEntity response = restTemplate
				.withBasicAuth("test_username", "test_password")
				.postForEntity("http://localhost:" + serverPort + "/project/update", "{}", Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
}
