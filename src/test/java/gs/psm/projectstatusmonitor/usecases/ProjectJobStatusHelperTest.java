package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.models.JobStatus;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectJobStatusHelperTest {

    private ProjectJobStatusHelper projectJobStatusHelper;

    @Before
    public void setup() {
        projectJobStatusHelper = new ProjectJobStatusHelper();
    }

    @Test
    public void getUniqueJobCodes_givenAListOfProjectJobStatus_withNoDuplicates_returnsASetOfUniqueJobCodes() {
        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-3", "name-3", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-4", "name-4", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-5", "name-5", JobStatus.PASSED));

        Set<String> uniqueJobCodeSet = projectJobStatusHelper.getUniqueJobCodes(projectJobStatusList);

        assertThat(uniqueJobCodeSet.size()).isEqualTo(projectJobStatusList.size());
    }

    @Test
    public void getUniqueJobCodes_givenAListOfProjectJobStatus_withDuplicates_returnsASetOfUniqueJobCodes() {
        int numberOfUniqueJobCodes = 4;

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-3", "name-3", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-1", "name-4", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-5", "name-5", JobStatus.PASSED));

        Set<String> uniqueJobCodeSet = projectJobStatusHelper.getUniqueJobCodes(projectJobStatusList);

        assertThat(uniqueJobCodeSet.size()).isEqualTo(numberOfUniqueJobCodes);
    }

    @Test
    public void containsNoDuplicateJobCodes_givenAListOfProjectJobStatus_withNoDuplicates_returnsTrue() {
        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-3", "name-3", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-4", "name-4", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-5", "name-5", JobStatus.PASSED));

        assertThat(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).isTrue();
    }

    @Test
    public void containsNoDuplicateJobCodes_givenAListOfProjectJobStatus_withDuplicates_returnsFalse() {
        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();

        projectJobStatusList.add(createProjectJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-1", "name-3", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-4", "name-4", JobStatus.PASSED));
        projectJobStatusList.add(createProjectJobStatus("code-5", "name-5", JobStatus.PASSED));

        assertThat(projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)).isFalse();
    }

    private ProjectJobStatus createProjectJobStatus(String code, String name, JobStatus status) {
        return ProjectJobStatus.builder()
                .jobCode(code)
                .jobName(name)
                .jobStatus(status)
                .build();
    }
}