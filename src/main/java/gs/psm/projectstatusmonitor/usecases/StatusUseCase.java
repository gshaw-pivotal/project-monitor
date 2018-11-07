package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.ports.ProjectRepository;

import java.util.List;

public class StatusUseCase {

    private ProjectRepository projectRepository;

    public StatusUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectJobStatus> getJobStatus(String projectCode) {
        Project project = projectRepository.getProject(projectCode);
        return project.getJobStatusList();
    }
}
