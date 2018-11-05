package gs.psm.projectstatusmonitor.repositories;

import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.models.Project;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryProjectProjectRepositoryTest {

    private InMemoryProjectProjectRepository repository;

    private List<Project> expectedProjectList;

    @Before
    public void setup() {
        repository = new InMemoryProjectProjectRepository();

        expectedProjectList = new ArrayList<>();
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

    @Test
    public void listProjects_whenThereAreNoProjects_returnsAnEmptyList() {
        List<Project> projects = repository.listProjects();

        assertThat(projects.size()).isEqualTo(0);
    }

    @Test
    public void listProjects_whenThereIsOneProject_returnsAListWithJustThatProject() {
        Project newProject = Project.builder()
                .projectCode("code")
                .projectName("name")
                .build();

        repository.addProject(newProject);

        List<Project> projectList = repository.listProjects();

        assertThat(projectList.size()).isEqualTo(1);

        Project project = projectList.get(0);

        assertThat(project.getProjectCode()).isEqualTo(newProject.getProjectCode());
        assertThat(project.getProjectName()).isEqualTo(newProject.getProjectName());
    }

    @Test
    public void listProjects_whenThereAreMultipleProjects_returnsAListWithAllTheProjects() {
        int numberOfProjects = 5;

        for (int count = 0; count < numberOfProjects; count++) {
            addProjectToRepository(count);
        }

        List<Project> projectList = repository.listProjects();

        assertThat(projectList.size()).isEqualTo(numberOfProjects);
        assertThat(projectList.containsAll(expectedProjectList));
    }

    @Test
    public void getProject_givenAProjectCodeThatExists_returnsTheCorrespondingProject() {
        String projectCode = "code1";
        addProjectToRepository(1);

        Project returnedProject = repository.getProject(projectCode);

        assertThat(returnedProject.getProjectCode()).isEqualTo(projectCode);
        assertThat(returnedProject.getProjectName()).isEqualTo("name1");
    }

    @Test
    public void getProject_givenAProjectCodeThatDoesNotExist_returnsNull() {
        Project returnedProject = repository.getProject("notExisting");

        assertThat(returnedProject).isNull();
    }

    private void addProjectToRepository(int increment) {
        Project newProject = Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .build();

        repository.addProject(newProject);

        expectedProjectList.add(newProject);
    }
}