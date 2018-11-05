package gs.psm.projectstatusmonitor.repositories;

import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.models.Project;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryProjectProjectRepositoryTest {

    private InMemoryProjectProjectRepository repository;

    @Before
    public void setup() {
        repository = new InMemoryProjectProjectRepository();
    }

    @Test
    public void addProject_forANewProject_addsTheProject() {
        Project newProject = Project.builder()
                .projectCode("code")
                .projectName("name")
                .build();

        Project responseProject = repository.addProject(newProject);

        assertThat(responseProject).isNotNull();
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void addProject_forADuplicateProject_throwsProjectAlreadyExistsException() {
        Project newProject = Project.builder()
                .projectCode("code")
                .projectName("name")
                .build();

        repository.addProject(newProject);
        repository.addProject(newProject);
    }
}