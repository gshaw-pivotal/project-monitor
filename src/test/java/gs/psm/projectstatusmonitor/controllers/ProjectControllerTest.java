package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.usecases.ProjectUseCase;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    public void list_GET_whenThereAreNoProjects_returnsAnEmptyList() throws Exception {
        when(projectUseCase.listProjects()).thenReturn(Collections.emptyList());

        MvcResult response = mockMvc
                .perform(get("/list"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(response.getResponse().getContentAsString()).isEqualTo("[]");
    }

    @Test
    public void list_GET_whenThereAreProjects_returnsAListOfTheProjects() throws Exception {
        List<Project> projectList = new ArrayList<>();

        projectList.add(createProject(0));
        projectList.add(createProject(1));
        projectList.add(createProject(2));

        when(projectUseCase.listProjects()).thenReturn(projectList);

        MvcResult response = mockMvc
                .perform(get("/list"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(response.getResponse().getContentAsString()).isEqualTo(
                "[" +
                        "{\"projectCode\":\"code0\",\"projectName\":\"name0\"}," +
                        "{\"projectCode\":\"code1\",\"projectName\":\"name1\"}," +
                        "{\"projectCode\":\"code2\",\"projectName\":\"name2\"}" +
                "]"
        );
    }

    @Test
    public void project_GET_whenTheProjectCodeExists_returnsTheProject() throws Exception {
        String projectCode = "code";

        Project expectedProject = createProject(1);

        when(projectUseCase.getProject(projectCode)).thenReturn(expectedProject);

        MvcResult response = mockMvc
                .perform(get("/project/" + projectCode))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(response.getResponse().getContentAsString()).isEqualTo("{\"projectCode\":\"code1\",\"projectName\":\"name1\"}");
    }

    @Test
    public void project_GET_whenTheProjectCodeDoesNotExist_returnsNotFound() throws Exception {
        when(projectUseCase.getProject("notFoundCode")).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/project/notFoundCode"))
                .andExpect(status().isNotFound());
    }

    private String buildAddProjectRequestBody(String projectCode) {
        return "{" +
                "\"projectCode\": \"" + projectCode + "\"," +
                "\"projectName\": \"projectName\"" +
                "}";
    }

    private Project createProject(int increment) {
        return Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .build();
    }
}