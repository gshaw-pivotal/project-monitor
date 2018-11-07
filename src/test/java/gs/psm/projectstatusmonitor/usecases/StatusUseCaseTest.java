package gs.psm.projectstatusmonitor.usecases;

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
import static org.mockito.Mockito.when;

public class StatusUseCaseTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private StatusUseCase statusUseCase;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        statusUseCase = new StatusUseCase(projectRepository);
    }

    @Test
    public void getJobStatus_givenAProjectCodeThatExists_returnsAListOfJobStatus() {
        String projectCode = "code1";

        List<ProjectJobStatus> projectJobList = new ArrayList<>();
        projectJobList.add(createProjectJobStatus("job1", JobStatus.PASSED));
        projectJobList.add(createProjectJobStatus("job2", JobStatus.RUNNING));
        projectJobList.add(createProjectJobStatus("job3", JobStatus.FAILED));

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

        when(projectRepository.getProject(projectCode)).thenThrow(new ProjectNotFoundException());

        statusUseCase.getJobStatus(projectCode);
    }

    private ProjectJobStatus createProjectJobStatus(String name, JobStatus status) {
        return ProjectJobStatus.builder()
                .jobName(name)
                .jobStatus(status)
                .build();
    }
}