package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.usecases.ProjectUseCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectUseCase projectUseCase;

    @InjectMocks
    private ProjectController projectController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(projectController)
                .setControllerAdvice(ControllerAdvice.class)
                .build();
    }

    @Test
    public void add_POST_givenAIncorrectRequestBody_returns400() throws Exception {
        mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{incorrectly formatted json}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\": \"value\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_POST_givenAnEmptyRequestBody_returns400() throws Exception {
        mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_POST_givenARequestBodyWithAnEmptyValue_returns400() throws Exception {
        mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectName\": \"projectName\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_POST_forAProjectThatDoesNotAlreadyExist_returns201AndAnIdForProject() throws Exception {
        String projectCode = "proCode";

        doNothing().when(projectUseCase).addProject(any());

        mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildAddProjectRequestBody(projectCode)))
                .andExpect(status().isCreated());
    }

    @Test
    public void add_POST_forAProjectThatAlreadyExists_return400() throws Exception {
        doThrow(new ProjectAlreadyExistsException()).when(projectUseCase).addProject(any());

        mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildAddProjectRequestBody("proCodeDuplicate")))
                .andExpect(status().isBadRequest());
    }

    private String buildAddProjectRequestBody(String projectCode) {
        return "{" +
                "\"projectCode\": \"" + projectCode + "\"," +
                "\"projectName\": \"projectName\"" +
                "}";
    }
}