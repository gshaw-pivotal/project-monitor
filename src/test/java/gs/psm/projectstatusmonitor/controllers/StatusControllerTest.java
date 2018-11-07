package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.JobStatus;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.usecases.StatusUseCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        projectJobStatusList.add(createJobStatus("job1", JobStatus.RUNNING));
        projectJobStatusList.add(createJobStatus("job2", JobStatus.PASSED));
        projectJobStatusList.add(createJobStatus("job3", JobStatus.FAILED));

        when(statusUseCase.getJobStatus(projectCode)).thenReturn(projectJobStatusList);

        MvcResult response = mockMvc
                .perform(get("/status/" + projectCode))
                .andExpect(status().isOk())
                .andReturn();

        String returnedJobStatus = response.getResponse().getContentAsString();

        assertThat(returnedJobStatus).isEqualTo(
                "[" +
                    "{\"jobName\":\"job1\",\"jobStatus\":\"RUNNING\"}," +
                    "{\"jobName\":\"job2\",\"jobStatus\":\"PASSED\"}," +
                    "{\"jobName\":\"job3\",\"jobStatus\":\"FAILED\"}" +
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

    private ProjectJobStatus createJobStatus(String name, JobStatus status) {
        return ProjectJobStatus.builder()
                .jobName(name)
                .jobStatus(status)
                .build();
    }
}