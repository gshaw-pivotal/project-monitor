package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.exceptions.DeleteProjectException;
import gs.psm.projectstatusmonitor.exceptions.DuplicateJobCodeException;
import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.JobStatus;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
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
    private ProjectJobStatusHelper projectJobStatusHelper;

    @InjectMocks
    private ProjectUseCase projectUseCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        projectUseCase = new ProjectUseCase(projectRepository, projectJobStatusHelper);
    }

    @Test
    public void addProject_givenANewProject_withNoProjectJobStatusList_callsTheProjectRepository() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.addProject(project)).thenReturn(project);

        projectUseCase.addProject(project, "username");

        verify(projectRepository, times(1)).addProject(project);
    }

    @Test
    public void addProject_givenANewProject_withNoProjectJobStatusList_doesNotCallTheJobStatusHelper() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        projectUseCase.addProject(project, "username");

        verify(projectJobStatusHelper, never()).containsNoDuplicateJobCodes(any());
    }

    @Test
    public void addProject_givenANewProject_withAnEmptyProjectStatusList_doesNotCallTheJobStatusHelper() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .jobStatusList(Collections.emptyList())
                .build();

        projectUseCase.addProject(project, "username");

        verify(projectJobStatusHelper, never()).containsNoDuplicateJobCodes(any());
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void addProject_givenADuplicateProject_throwsProjectAlreadyExistsException() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.addProject(project)).thenThrow(new ProjectAlreadyExistsException());

        projectUseCase.addProject(project, "username");
    }

    @Test
    public void addProject_givenANewProject_withAListOfProjectJobStatus_withNoDuplicateJobCodes_callsTheJobStatusHelper() {
        List<ProjectJobStatus> jobStatusList = new ArrayList<>();

        jobStatusList.add(createJobStatus("jobCode1", "job-name-1", JobStatus.PASSED));
        jobStatusList.add(createJobStatus("jobCode2", "job-name-2", JobStatus.PASSED));
        jobStatusList.add(createJobStatus("jobCode3", "job-name-3", JobStatus.PASSED));

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .jobStatusList(jobStatusList)
                .build();

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(jobStatusList)).thenReturn(true);

        projectUseCase.addProject(project, "username");

        verify(projectJobStatusHelper, times(1)).containsNoDuplicateJobCodes(jobStatusList);
    }

    @Test(expected = DuplicateJobCodeException.class)
    public void addProject_givenANewProject_withDuplicateJobCode_throwsDuplicateJobCodeException() {
        List<ProjectJobStatus> jobStatusList = new ArrayList<>();

        jobStatusList.add(createJobStatus("jobCode1", "job-name-1", JobStatus.PASSED));
        jobStatusList.add(createJobStatus("jobCode1", "job-name-2", JobStatus.PASSED));
        jobStatusList.add(createJobStatus("jobCode3", "job-name-3", JobStatus.PASSED));

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .jobStatusList(jobStatusList)
                .build();

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(jobStatusList)).thenReturn(false);

        projectUseCase.addProject(project, "username");
    }

    @Test
    public void addProject_givenANewProjectThatIsValid_callsTheProjectRepositoryToAssociateUserWithProject() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.addProject(project)).thenReturn(project);

        projectUseCase.addProject(project, "username");

        verify(projectRepository, times(1)).addProject(project);
        verify(projectRepository, times(1)).associateUserWithProject("username", "projectCode");
    }

    @Test
    public void addProject_givenThatAProjectAlreadyExistsExceptionIsThrown_doesNotCallTheRepositoryToAssociateUserWithProject() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.addProject(project)).thenThrow(new ProjectAlreadyExistsException());

        try {
            projectUseCase.addProject(project, "username");
        } catch (ProjectAlreadyExistsException e) {

        } finally {
            verify(projectRepository, never()).associateUserWithProject(anyString(), anyString());
        }
    }

    @Test
    public void addProject_givenThatDuplicateJobCodeExceptionIsThrown_doesNotCallTheRepositoryToAssociateUserWithProject() {
        List<ProjectJobStatus> jobStatusList = new ArrayList<>();

        jobStatusList.add(createJobStatus("jobCode1", "job-name-1", JobStatus.PASSED));
        jobStatusList.add(createJobStatus("jobCode1", "job-name-2", JobStatus.PASSED));
        jobStatusList.add(createJobStatus("jobCode3", "job-name-3", JobStatus.PASSED));

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .jobStatusList(jobStatusList)
                .build();

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(jobStatusList)).thenReturn(false);

        try {
            projectUseCase.addProject(project, "username");
        } catch (DuplicateJobCodeException e) {

        } finally {
            verify(projectRepository, never()).associateUserWithProject(anyString(), anyString());
        }
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
    public void updateProject_givenAProjectWithAProjectCodeThatExists_withNoProjectJobStatusList_callsTheProjectRepository() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.updateProject(project)).thenReturn(project);

        projectUseCase.updateProject(project);

        verify(projectRepository, times(1)).updateProject(project);
    }

    @Test
    public void updateProject_givenAProjectWithAProjectCodeThatExists_withNoProjectJobStatusList_doesNotCallTheJobStatusHelper() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        projectUseCase.updateProject(project);

        verify(projectJobStatusHelper, never()).containsNoDuplicateJobCodes(any());
    }

    @Test
    public void updateProject_givenAProjectWithAProjectCodeThatExists_withAnEmptyProjectJobStatusList_doesNotCallTheJobStatusHelper() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .jobStatusList(Collections.emptyList())
                .build();

        projectUseCase.updateProject(project);

        verify(projectJobStatusHelper, never()).containsNoDuplicateJobCodes(any());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void updateProject_givenAProjectWithAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        when(projectRepository.updateProject(project)).thenThrow(new ProjectNotFoundException());

        projectUseCase.updateProject(project);
    }

    @Test
    public void updateProject_givenAProjectWithAProjectCodeThatExists_andAListOfProjectJobStatus_withNoDuplicateJobCodes_callsTheProjectRepository() {
        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        projectJobStatusList.add(createJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-3", "name-3", JobStatus.PASSED));

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .jobStatusList(projectJobStatusList)
                .build();

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).thenReturn(true);

        projectUseCase.updateProject(project);

        verify(projectRepository, times(1)).updateProject(project);
    }

    @Test(expected = DuplicateJobCodeException.class)
    public void updateProject_givenAProjectWithAProjectCodeThatExists_andAListOfProjectJobStatus_withDuplicateJobCodes_throwsDuplicateJobCodeException() {
        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        projectJobStatusList.add(createJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-1", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-3", "name-3", JobStatus.PASSED));

        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .jobStatusList(projectJobStatusList)
                .build();

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).thenReturn(false);

        projectUseCase.updateProject(project);
    }

    private Project createProject(int increment) {
        return Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .build();
    }

    private ProjectJobStatus createJobStatus(String code, String name, JobStatus status) {
        return ProjectJobStatus.builder()
                .jobCode(code)
                .jobName(name)
                .jobStatus(status)
                .build();
    }
}