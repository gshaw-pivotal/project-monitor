package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.exceptions.DeleteProjectException;
import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.ports.ProjectRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ProjectUseCaseTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectUseCase projectUseCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        projectUseCase = new ProjectUseCase(projectRepository);
    }

    @Test
    public void addProject_givenANewProject_callsTheProjectRepository() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.addProject(project)).thenReturn(project);

        projectUseCase.addProject(project);

        verify(projectRepository, times(1)).addProject(project);
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void addProject_givenADuplicateProject_throwsProjectAlreadyExistsException() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.addProject(project)).thenThrow(new ProjectAlreadyExistsException());

        projectUseCase.addProject(project);
    }

    @Test
    public void listProjects_callsTheProjectRepository() {
        when(projectRepository.listProjects()).thenReturn(Collections.emptyList());

        projectUseCase.listProjects();

        verify(projectRepository, times(1)).listProjects();
    }

    @Test
    public void listProjects_whenThereAreNoProjects_returnsAnEmptyList() {
        List<Project> expectedProjects = new ArrayList<>();

        when(projectRepository.listProjects()).thenReturn(expectedProjects);

        List<Project> returnedProjects = projectUseCase.listProjects();

        assertThat(returnedProjects.size()).isEqualTo(expectedProjects.size());
    }

    @Test
    public void listProjects_whenThereAreProjects_returnsTheListOfProjects() {
        List<Project> expectedProjects = new ArrayList<>();

        expectedProjects.add(createProject(0));
        expectedProjects.add(createProject(1));
        expectedProjects.add(createProject(2));

        when(projectRepository.listProjects()).thenReturn(expectedProjects);

        List<Project> returnedProjects = projectUseCase.listProjects();

        assertThat(returnedProjects.size()).isEqualTo(expectedProjects.size());
        assertThat(returnedProjects.containsAll(expectedProjects));
    }

    @Test
    public void getProject_givenAProjectCodeThatExists_returnsThatProject() {
        String projectCode = "code";

        Project expectedProject = Project.builder()
                .projectCode(projectCode)
                .projectName("projectName")
                .build();

        when(projectRepository.getProject(projectCode)).thenReturn(expectedProject);

        Project returnedProject = projectUseCase.getProject(projectCode);

        assertThat(returnedProject.getProjectCode()).isEqualTo(expectedProject.getProjectCode());
        assertThat(returnedProject.getProjectName()).isEqualTo(expectedProject.getProjectName());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void getProject_givenAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        when(projectRepository.getProject(any())).thenReturn(null);

        projectUseCase.getProject("codeNotFound");
    }

    @Test
    public void removeProject_givenAProjectCodeThatExists_callsTheProjectRepository() {
        String projectCode = "codeToDelete";

        when(projectRepository.removeProject(projectCode)).thenReturn(true);

        projectUseCase.removeProject(projectCode);

        verify(projectRepository, times(1)).removeProject(projectCode);
    }

    @Test(expected = DeleteProjectException.class)
    public void removeProject_givenTheRepositoryReturnsFalse_throwsDeleteProjectException() {
        String projectCode = "codeToDelete";

        when(projectRepository.removeProject(projectCode)).thenReturn(false);

        projectUseCase.removeProject(projectCode);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void removeProject_givenAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        when(projectRepository.removeProject("codeNotFound")).thenThrow(new ProjectNotFoundException());

        projectUseCase.removeProject("codeNotFound");
    }

    @Test
    public void updateProject_givenAProjectWithAProjectCodeThatExists_callsTheProjectRepository() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.updateProject(project)).thenReturn(project);

        projectUseCase.updateProject(project);

        verify(projectRepository, times(1)).updateProject(project);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void updateProject_givenAProjectWithAProjectCodeThatDoesNotExist_thorwsProjectNotFoundException() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.updateProject(project)).thenThrow(new ProjectNotFoundException());

        projectUseCase.updateProject(project);
    }

    private Project createProject(int increment) {
        return Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .build();
    }
}