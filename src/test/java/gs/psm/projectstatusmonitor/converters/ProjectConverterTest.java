package gs.psm.projectstatusmonitor.converters;

import gs.psm.projectstatusmonitor.models.AddProjectRequest;
import gs.psm.projectstatusmonitor.models.AddProjectResponse;
import gs.psm.projectstatusmonitor.models.Project;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectConverterTest {

    private ProjectConverter converter;

    @Before
    public void setup() {
        converter = new ProjectConverter();
    }

    @Test
    public void convertAddProjectRequestToProject() {
        AddProjectRequest addProjectRequest = AddProjectRequest.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        Project converted = converter.convertRequest(addProjectRequest);

        assertThat(converted.getProjectCode()).isEqualTo("projectCode");
        assertThat(converted.getProjectName()).isEqualTo("projectName");
    }

    @Test
    public void convertProjectToAddProjectResponse() {
        Project project = Project.builder()
                .projectCode("projectCode")
                .projectName("projectName")
                .build();

        AddProjectResponse converted = converter.convertProject(project);

        assertThat(converted.getProjectCode()).isEqualTo("projectCode");
        assertThat(converted.getProjectName()).isEqualTo("projectName");
    }
}