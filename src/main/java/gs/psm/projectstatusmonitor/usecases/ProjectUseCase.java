package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.converters.ProjectConverter;
import gs.psm.projectstatusmonitor.models.AddProjectRequest;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.ports.ProjectRepository;

import java.util.List;

public class ProjectUseCase {

    private ProjectRepository projectRepository;
    private ProjectConverter projectConverter;

    public ProjectUseCase(ProjectRepository projectRepository, ProjectConverter projectConverter) {
        this.projectRepository = projectRepository;
        this.projectConverter = projectConverter;
    }

    public void addProject(AddProjectRequest projectRequest) {
        Project project = projectConverter.convertRequest(projectRequest);

        projectRepository.addProject(project);
    }

    public List<Project> listProjects() {
        return projectRepository.listProjects();
    }
}
