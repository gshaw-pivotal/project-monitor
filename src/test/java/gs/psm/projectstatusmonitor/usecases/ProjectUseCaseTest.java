package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.converters.ProjectConverter;
import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.models.AddProjectRequest;
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

    @Mock
    private ProjectConverter projectConverter;

    @InjectMocks
    private ProjectUseCase projectUseCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        projectUseCase = new ProjectUseCase(projectRepository, projectConverter);
    }

    @Test
    public void addProject_givenANewProject_callsTheProjectConverter() {
        AddProjectRequest request = AddProjectRequest.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectConverter.convertRequest(request)).thenReturn(project);

        projectUseCase.addProject(request);

        verify(projectConverter, times(1)).convertRequest(request);
    }

    @Test
    public void addProject_givenANewProject_callsTheProjectRepository() {
        AddProjectRequest request = AddProjectRequest.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectConverter.convertRequest(request)).thenReturn(project);
        when(projectRepository.addProject(project)).thenReturn(project);

        projectUseCase.addProject(request);

        verify(projectRepository, times(1)).addProject(project);
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void addProject_givenADuplicateProject_throwsProjectAlreadyExistsException() {
        AddProjectRequest request = AddProjectRequest.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectConverter.convertRequest(request)).thenReturn(project);
        when(projectRepository.addProject(project)).thenThrow(new ProjectAlreadyExistsException());

        projectUseCase.addProject(request);
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

    private Project createProject(int increment) {
        return Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .build();
    }
}