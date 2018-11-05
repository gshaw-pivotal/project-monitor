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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}