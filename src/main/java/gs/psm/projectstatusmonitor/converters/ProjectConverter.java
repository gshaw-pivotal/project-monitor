package gs.psm.projectstatusmonitor.converters;

import gs.psm.projectstatusmonitor.models.AddProjectRequest;
import gs.psm.projectstatusmonitor.models.AddProjectResponse;
import gs.psm.projectstatusmonitor.models.Project;

public class ProjectConverter {
    public Project convertRequest(AddProjectRequest addProjectRequest) {
        Project convertedProject = Project.builder()
                .projectCode(addProjectRequest.getProjectCode())
                .projectName(addProjectRequest.getProjectName())
                .build();
        return convertedProject;
    }

    public AddProjectResponse convertProject(Project project) {
        AddProjectResponse convertedResponse = AddProjectResponse.builder()
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .build();

        return convertedResponse;
    }
}
