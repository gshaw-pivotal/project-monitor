package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.exceptions.DuplicateJobCodeException;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class StatusUseCaseTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectJobStatusHelper projectJobStatusHelper;

    @InjectMocks
    private StatusUseCase statusUseCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        statusUseCase = new StatusUseCase(projectRepository, projectJobStatusHelper);
    }

    @Test
    public void getJobStatus_givenAProjectCodeThatExists_returnsAListOfJobStatus() {
        String projectCode = "code1";

        List<ProjectJobStatus> projectJobList = new ArrayList<>();
        projectJobList.add(createProjectJobStatus("code1","job1", JobStatus.PASSED));
        projectJobList.add(createProjectJobStatus("code2","job2", JobStatus.RUNNING));
        projectJobList.add(createProjectJobStatus("code3","job3", JobStatus.FAILED));

        Project project = Project.builder()
                .projectCode(projectCode)
                .projectName("name")
                .jobStatusList(projectJobList)
                .build();

        when(projectRepository.getProject(projectCode)).thenReturn(project);

        List<ProjectJobStatus> returnedJobs = statusUseCase.getJobStatus(projectCode);

        assertThat(returnedJobs.size()).isEqualTo(projectJobList.size());
        assertThat(returnedJobs.containsAll(projectJobList));
    }

    @Test
    public void getJobStatus_givenAProjectCodeThatExists_andTheProjectHasNoJobs_returnsAnEmptyListOfJobStatus() {
        String projectCode = "code1";

        List<ProjectJobStatus> projectJobList = new ArrayList<>();

        Project project = Project.builder()
                .projectCode(projectCode)
                .projectName("name")
                .jobStatusList(projectJobList)
                .build();

        when(projectRepository.getProject(projectCode)).thenReturn(project);

        List<ProjectJobStatus> returnedJobs = statusUseCase.getJobStatus(projectCode);

        assertThat(returnedJobs.size()).isEqualTo(0);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void getJobStatus_givenAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        String projectCode = "code1";

        when(projectRepository.getProject(projectCode)).thenReturn(null);

        statusUseCase.getJobStatus(projectCode);
    }

    @Test
    public void updateJobStatusList_givenAProjectCode_andAValidProjectJobStatusList_callsTheJobStatusHelper() {
        String projectCode = "projectCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();
        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-3", "name-3", JobStatus.PASSED));

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).thenReturn(true);

        statusUseCase.updateProjectJobs(projectCode, projectJobStatusList);

        verify(projectJobStatusHelper, times(1)).containsNoDuplicateJobCodes(projectJobStatusList);
    }

    @Test
    public void updateJobStatusList_givenAProjectCode_andAValidProjectJobStatusList_callsTheRepository() {
        String projectCode = "projectCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();
        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-3", "name-3", JobStatus.PASSED));

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).thenReturn(true);

        statusUseCase.updateProjectJobs(projectCode, projectJobStatusList);

        verify(projectRepository, times(1)).updateProjectJobs(projectCode, projectJobStatusList);
    }

    @Test
    public void updateJobStatusList_givenAProjectCode_andAnEmptyProjectJobStatusList_doesNotCallTheJobStatusHelper() {
        String projectCode = "projectCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        statusUseCase.updateProjectJobs(projectCode, projectJobStatusList);

        verify(projectJobStatusHelper, never()).containsNoDuplicateJobCodes(any());
    }

    @Test
    public void updateJobStatusList_givenAProjectCode_andAnEmptyProjectJobStatusList_callsTheRepository() {
        String projectCode = "projectCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        statusUseCase.updateProjectJobs(projectCode, projectJobStatusList);

        verify(projectRepository, times(1)).updateProjectJobs(projectCode, projectJobStatusList);
    }

    @Test(expected = DuplicateJobCodeException.class)
    public void updateJobStatusList_givenAProjectCodeThatExists_andAProjectStatusList_thatHasDuplicateJobCodes_throwsDuplicateJobCodeException() {
        String projectCode = "projectCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();
        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-1", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-3", "name-3", JobStatus.PASSED));

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).thenReturn(false);

        statusUseCase.updateProjectJobs(projectCode, projectJobStatusList);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void updateJobStatusList_givenAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        String projectCode = "projectCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();
        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-3", "name-3", JobStatus.PASSED));

        when(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).thenReturn(true);
        when(projectRepository.updateProjectJobs(projectCode, projectJobStatusList)).thenThrow(new ProjectNotFoundException());

        statusUseCase.updateProjectJobs(projectCode, projectJobStatusList);
    }

    private ProjectJobStatus createProjectJobStatus(String code, String name, JobStatus status) {
        return ProjectJobStatus.builder()
                .jobCode(code)
                .jobName(name)
                .jobStatus(status)
                .build();
    }
}