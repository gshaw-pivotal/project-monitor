package gs.psm.projectstatusmonitor;

import gs.psm.projectstatusmonitor.models.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectStatusMonitorApplicationTests {

	@LocalServerPort
	private int serverPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void updateProject_whenTheUserIsNotAssociatedWithTheProject_receives400Response() {
		Project project = createProject(1);
		ResponseEntity response;

		response = restTemplate
				.withBasicAuth("test_username", "test_password")
				.postForEntity("http://localhost:" + serverPort + "/project/add", project, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		project.setProjectName("Name name");
		response = restTemplate
				.withBasicAuth("test_username2", "test_password2")
				.postForEntity("http://localhost:" + serverPort + "/project/update", project, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void deleteProject_whenTheUserIsNotAssociatedWithTheProject_receives400Response() {
		Project project = createProject(2);
		ResponseEntity response;

		response = restTemplate
				.withBasicAuth("test_username", "test_password")
				.postForEntity("http://localhost:" + serverPort + "/project/add", project, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		response = restTemplate
				.withBasicAuth("test_username2", "test_password2")
				.exchange("http://localhost:" + serverPort + "/project/" + project.getProjectCode(), HttpMethod.DELETE, null, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	private Project createProject(int increment) {
		return Project.builder()
				.projectCode("code" + increment)
				.projectName("name" + increment)
				.jobStatusList(null)
				.build();
	}
}
