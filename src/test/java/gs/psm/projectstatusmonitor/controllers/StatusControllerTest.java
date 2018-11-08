package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.exceptions.DuplicateJobCodeException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.JobStatus;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.usecases.StatusUseCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatusControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StatusUseCase statusUseCase;

    @InjectMocks
    private StatusController statusController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(statusController)
                .setControllerAdvice(ControllerAdvice.class)
                .build();
    }

    @Test
    public void status_GET_callsTheStatusUseCase() throws Exception {
        when(statusUseCase.getJobStatus("code1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/status/code1"))
                .andExpect(status().isOk());

        verify(statusUseCase, times(1)).getJobStatus("code1");
    }

    @Test
    public void status_GET_givenAProjectCodeThatExists_returnsAListOfJobsForThatProject() throws Exception {
        String projectCode = "code1";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();
        projectJobStatusList.add(createJobStatus("job-code-1", "job1", JobStatus.RUNNING));
        projectJobStatusList.add(createJobStatus("job-code-2", "job2", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("job-code-3", "job3", JobStatus.FAILED));

        when(statusUseCase.getJobStatus(projectCode)).thenReturn(projectJobStatusList);

        MvcResult response = mockMvc
                .perform(get("/status/" + projectCode))
                .andExpect(status().isOk())
                .andReturn();

        String returnedJobStatus = response.getResponse().getContentAsString();

        assertThat(returnedJobStatus).isEqualTo(
                "[" +
                        "{\"jobCode\":\"job-code-1\",\"jobName\":\"job1\",\"jobStatus\":\"RUNNING\"}," +
                        "{\"jobCode\":\"job-code-2\",\"jobName\":\"job2\",\"jobStatus\":\"PASSED\"}," +
                        "{\"jobCode\":\"job-code-3\",\"jobName\":\"job3\",\"jobStatus\":\"FAILED\"}" +
                        "]"
        );
    }

    @Test
    public void status_GET_givenAProjectCodeThatDoesNotExist_returns400() throws Exception {
        String projectCode = "code1";

        when(statusUseCase.getJobStatus(projectCode)).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/status/" + projectCode))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void statusUpdateList_POST_givenAProjectCodeThatExists_andAValidProjectJobStatusList_callsTheStatusUseCase() throws Exception {
        String projectCode = "projectCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();
        projectJobStatusList.add(createJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-2", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-3", "name-3", JobStatus.PASSED));

        doNothing().when(statusUseCase).updateProjectJobs(projectCode, projectJobStatusList);

        mockMvc.perform(post("/status/updateList/" + projectCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectJobStatusListAsJson()))
                .andExpect(status().isOk());

        verify(statusUseCase, times(1)).updateProjectJobs(projectCode, projectJobStatusList);
    }

    @Test
    public void statusUpdateList_POST_givenAProjectCodeThatExists_andAnEmptyProjectJobStatusList_returns200() throws Exception {
        String projectCode = "projectCode";

        doNothing().when(statusUseCase).updateProjectJobs(projectCode, Collections.emptyList());

        mockMvc.perform(post("/status/updateList/" + projectCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildEmptyProjectJobStatusListAsJson()))
                .andExpect(status().isOk());
    }

    @Test
    public void statusUpdateList_POST_givenAProjectCodeThatExists_andAProjectJobStatusList_whereAJobCodeIsMissing_returns400() throws Exception {
        String projectCode = "projectCode";

        mockMvc.perform(post("/status/updateList/" + projectCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectJobStatusListWithMissingJobCodeAsJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void statusUpdateList_POST_givenAProjectCodeThatExists_andAProjectJobStatusList_whereAJobStatusIsInvalid_returns400() throws Exception {
        String projectCode = "projectCode";

        mockMvc.perform(post("/status/updateList/" + projectCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectJobStatusListWithInvalidJobStatusAsJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void statusUpdateList_POST_givenAProjectCodeThatExists_andAProjectJobStatusList_withDuplicateJobCodes_returns400() throws Exception {
        String projectCode = "proCode";

        List<ProjectJobStatus> projectJobStatusList = new ArrayList<>();
        projectJobStatusList.add(createJobStatus("code-1", "name-1", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-1", "name-2", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("code-3", "name-3", JobStatus.PASSED));

        doThrow(new DuplicateJobCodeException()).when(statusUseCase).updateProjectJobs(projectCode, projectJobStatusList);

        mockMvc.perform(post("/status/updateList/" + projectCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectJobStatusListWithDuplicateJobCodesAsJson()))
                .andExpect(status().isBadRequest());
    }

    private ProjectJobStatus createJobStatus(String code, String name, JobStatus status) {
        return ProjectJobStatus.builder()
                .jobCode(code)
                .jobName(name)
                .jobStatus(status)
                .build();
    }

    private String buildProjectJobStatusListAsJson() {
        return "{" +
                    "\"projectJobStatusList\":" +
                    "[" +
                        "{" +
                            "\"jobCode\": \"code-1\"," +
                            "\"jobName\": \"name-1\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}," +
                        "{" +
                            "\"jobCode\": \"code-2\"," +
                            "\"jobName\": \"name-2\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}," +
                        "{" +
                            "\"jobCode\": \"code-3\"," +
                            "\"jobName\": \"name-3\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}" +
                    "]" +
                "}";
    }

    private String buildEmptyProjectJobStatusListAsJson() {
        return "{" +
                    "\"projectJobStatusList\": []" +
                "}";
    }

    private String buildProjectJobStatusListWithDuplicateJobCodesAsJson() {
        return "{" +
                    "\"projectJobStatusList\":" +
                    "[" +
                        "{" +
                            "\"jobCode\": \"code-1\"," +
                            "\"jobName\": \"name-1\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}," +
                        "{" +
                            "\"jobCode\": \"code-1\"," +
                            "\"jobName\": \"name-2\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}," +
                        "{" +
                            "\"jobCode\": \"code-3\"," +
                            "\"jobName\": \"name-3\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}" +
                    "]" +
                "}";
    }

    private String buildProjectJobStatusListWithMissingJobCodeAsJson() {
        return "{" +
                    "\"projectJobStatusList\":" +
                    "[" +
                        "{" +
                            "\"jobName\": \"name-1\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}," +
                        "{" +
                            "\"jobName\": \"name-2\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}," +
                        "{" +
                            "\"jobName\": \"name-3\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}" +
                    "]" +
                "}";
    }

    private String buildProjectJobStatusListWithInvalidJobStatusAsJson() {
        return "{" +
                    "\"projectJobStatusList\":" +
                    "[" +
                        "{" +
                            "\"jobCode\": \"code-1\"," +
                            "\"jobName\": \"name-1\"," +
                            "\"jobStatus\": \"WRONG\"" +
                        "}," +
                        "{" +
                            "\"jobCode\": \"code-2\"," +
                            "\"jobName\": \"name-2\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}," +
                        "{" +
                            "\"jobCode\": \"code-3\"," +
                            "\"jobName\": \"name-3\"," +
                            "\"jobStatus\": \"PASSED\"" +
                        "}" +
                    "]" +
                "}";
    }
}