package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.exceptions.*;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{incorrectly formatted json}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/project/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\": \"value\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_POST_givenAnEmptyRequestBody_returns400() throws Exception {
        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_POST_givenARequestBodyWithAnEmptyValue_returns400() throws Exception {
        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectName\": \"projectName\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_POST_forAProjectThatDoesNotAlreadyExist_andContainsNoJobStatusList_returns201() throws Exception {
        String projectCode = "proCode";

        doNothing().when(projectUseCase).addProject(any(), eq("username"));

        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectAsJson(projectCode)))
                .andExpect(status().isCreated());

        verify(projectUseCase, times(1)).addProject(any(), eq("username"));
    }

    @Test
    public void add_POST_forAProjectThatDoesNotAlreadyExist_andContainsAJobStatusList_returns201() throws Exception {
        String projectCode = "proCode";

        doNothing().when(projectUseCase).addProject(any(), eq("username"));

        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectWithStatusListAsJson(projectCode)))
                .andExpect(status().isCreated());

        verify(projectUseCase, times(1)).addProject(any(), eq("username"));
    }

    @Test
    public void add_POST_forAProjectThatDoesNotAlreadyExist_andContainsAJobStatusList_withAMissingJobCode_returns400() throws Exception {
        String projectCode = "proCode";

        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectWithStatusListMissingJobCodeAsJson(projectCode)))
                .andExpect(status().isBadRequest());

        verify(projectUseCase, never()).addProject(any(), anyString());
    }

    @Test
    public void add_POST_forAProjectThatDoesNotAlreadyExist_andContainsAJobStatusList_withInvalidStatus_returns400() throws Exception {
        String projectCode = "proCode";

        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectWithStatusListContainingAnInvalidJobStatusAsJson(projectCode)))
                .andExpect(status().isBadRequest());

        verify(projectUseCase, never()).addProject(any(), anyString());
    }

    @Test
    public void add_POST_forAProjectThatDoesNotAlreadyExist_andContainsAJobStatusList_withDuplicateJobCodes_returns400() throws Exception {
        String projectCode = "proCode";

        doThrow(new DuplicateJobCodeException()).when(projectUseCase).addProject(any(), eq("username"));

        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectWithStatusListContainingDuplicateJobCodesAsJson(projectCode)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void add_POST_forAProjectThatAlreadyExists_return400() throws Exception {
        doThrow(new ProjectAlreadyExistsException()).when(projectUseCase).addProject(any(), eq("username"));

        mockMvc.perform(post("/project/add")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectAsJson("proCodeDuplicate")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void list_GET_whenThereAreNoProjects_returnsAnEmptyList() throws Exception {
        when(projectUseCase.listProjects()).thenReturn(Collections.emptyList());

        MvcResult response = mockMvc
                .perform(get("/project/list"))
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
                .perform(get("/project/list"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(response.getResponse().getContentAsString()).isEqualTo(
                "[" +
                        "{\"projectCode\":\"code0\",\"projectName\":\"name0\",\"jobStatusList\":null}," +
                        "{\"projectCode\":\"code1\",\"projectName\":\"name1\",\"jobStatusList\":null}," +
                        "{\"projectCode\":\"code2\",\"projectName\":\"name2\",\"jobStatusList\":null}" +
                "]"
        );
    }

    @Test
    public void project_GET_whenTheProjectCodeExists_returnsTheProject() throws Exception {
        String projectCode = "code";

        Project expectedProject = createProject(1);

        when(projectUseCase.getProject(projectCode, "username")).thenReturn(expectedProject);

        MvcResult response = mockMvc
                .perform(get("/project/" + projectCode)
                .with(httpBasic("username", "password")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(response.getResponse().getContentAsString())
                .isEqualTo("{\"projectCode\":\"code1\",\"projectName\":\"name1\",\"jobStatusList\":null}");
    }

    @Test
    public void project_GET_whenTheProjectCodeDoesNotExist_returns400() throws Exception {
        when(projectUseCase.getProject("notFoundCode", "username")).thenThrow(new ProjectNotFoundException());

        mockMvc.perform(get("/project/notFoundCode")
                .with(httpBasic("username", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void project_GET_whenUserIsNotAssociatedWithProject_returns400() throws Exception {
        String projectCode = "code";
        String username = "username";

        doThrow(new UserActionNotAllowedException()).when(projectUseCase).getProject(projectCode, username);

        mockMvc
                .perform(get("/project/" + projectCode)
                        .with(httpBasic(username, "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void project_DELETE_whenTheProjectCodeExists_returns204() throws Exception {
        String projectCode = "projectCode";

        doNothing().when(projectUseCase).removeProject(projectCode, "username");

        mockMvc.perform(delete("/project/" + projectCode)
                .with(httpBasic("username", "password")))
                .andExpect(status().isNoContent());
    }

    @Test
    public void project_DELETE_whenTheProjectCodeDoesNotExists_returns400() throws Exception {
        String projectCode = "projectCode";

        doThrow(new ProjectNotFoundException()).when(projectUseCase).removeProject(projectCode, "username");

        mockMvc.perform(delete("/project/" + projectCode)
                .with(httpBasic("username", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void project_DELETE_whenTheRemoveOperationFails_returns500() throws Exception {
        String projectCode = "projectCode";

        doThrow(new DeleteProjectException()).when(projectUseCase).removeProject(projectCode, "username");

        mockMvc.perform(delete("/project/" + projectCode)
                .with(httpBasic("username", "password")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void project_DELETE_whenUserIsNotAssociatedWithProject_returns400() throws Exception {
        String projectCode = "projectCode";

        doThrow(new UserActionNotAllowedException()).when(projectUseCase).removeProject(projectCode, "username");

        mockMvc.perform(delete("/project/" + projectCode)
                .with(httpBasic("username", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_POST_givenAValidProjectThatAlreadyExists_returns200() throws Exception {
        String projectCode = "proCode";

        doNothing().when(projectUseCase).updateProject(any(), eq("username"));

        mockMvc.perform(post("/project/update")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectAsJson(projectCode)))
                .andExpect(status().isOk());
    }

    @Test
    public void update_POST_givenAnInvalidProject_returns400() throws Exception {
        mockMvc.perform(post("/project/update")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"projectName\": \"projectName\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_POST_givenAValidProjectThatDoesNotExist_returns400() throws Exception {
        String projectCode = "proCode";

        doThrow(new ProjectNotFoundException()).when(projectUseCase).updateProject(any(), eq("username"));

        mockMvc.perform(post("/project/update")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectAsJson(projectCode)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_POST_givenUserIsNotAssociatedWithProject_returns400() throws Exception {
        String projectCode = "proCode";

        doThrow(new UserActionNotAllowedException()).when(projectUseCase).updateProject(any(), eq("username"));

        mockMvc.perform(post("/project/update")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildProjectAsJson(projectCode)))
                .andExpect(status().isBadRequest());
    }

    private String buildProjectAsJson(String projectCode) {
        return "{" +
                "\"projectCode\": \"" + projectCode + "\"," +
                "\"projectName\": \"projectName\"" +
                "}";
    }

    private String buildProjectWithStatusListAsJson(String projectCode) {
        return "{" +
                    "\"projectCode\": \"" + projectCode + "\"," +
                    "\"projectName\": \"projectName\"," +
                    "\"jobStatusList\": [" +
                        "{" +
                            "\"jobCode\": \"a job code\"," +
                            "\"jobName\": \"a job name\"," +
                            "\"jobStatus\": \"RUNNING\"" +
                        "}" +
                    "]" +
                "}";
    }

    private String buildProjectWithStatusListMissingJobCodeAsJson(String projectCode) {
        return "{" +
                    "\"projectCode\": \"" + projectCode + "\"," +
                    "\"projectName\": \"projectName\"," +
                    "\"jobStatusList\": [" +
                        "{" +
                            "\"jobName\": \"a job name\"," +
                            "\"jobStatus\": \"RUNNING\"" +
                        "}" +
                    "]" +
                "}";
    }

    private String buildProjectWithStatusListContainingAnInvalidJobStatusAsJson(String projectCode) {
        return "{" +
                    "\"projectCode\": \"" + projectCode + "\"," +
                    "\"projectName\": \"projectName\"," +
                    "\"jobStatusList\": [" +
                        "{" +
                            "\"jobCode\": \"a job code\"," +
                            "\"jobName\": \"a job name\"," +
                            "\"jobStatus\": \"WRONG\"" +
                        "}" +
                    "]" +
                "}";
    }

    private String buildProjectWithStatusListContainingDuplicateJobCodesAsJson(String projectCode) {
        return "{" +
                    "\"projectCode\": \"" + projectCode + "\"," +
                    "\"projectName\": \"projectName\"," +
                    "\"jobStatusList\": [" +
                        "{" +
                            "\"jobCode\": \"code-1\"," +
                            "\"jobName\": \"name-1\"," +
                            "\"jobStatus\": \"RUNNING\"" +
                        "},{" +
                            "\"jobCode\": \"code-1\"," +
                            "\"jobName\": \"name-2\"," +
                            "\"jobStatus\": \"RUNNING\"" +
                        "}" +
                    "]" +
                "}";
    }

    private Project createProject(int increment) {
        return Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .jobStatusList(null)
                .build();
    }
}