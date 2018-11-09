package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.exceptions.DuplicateJobCodeException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.ports.ProjectRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatusUseCase {

    private ProjectRepository projectRepository;

    private ProjectJobStatusHelper projectJobStatusHelper;

    public StatusUseCase(
            ProjectRepository projectRepository,
            ProjectJobStatusHelper projectJobStatusHelper
    ) {
        this.projectRepository = projectRepository;
        this.projectJobStatusHelper = projectJobStatusHelper;
    }

    public List<ProjectJobStatus> getJobStatus(String projectCode) {
        Project project = projectRepository.getProject(projectCode);

        if (project != null) {
            return project.getJobStatusList();
        }

        throw new ProjectNotFoundException();
    }

    public void updateProjectJobs(String projectCode, List<ProjectJobStatus> projectJobStatusList) {

        if (projectJobStatusList.size() > 0) {
            if (!projectJobStatusHelper.containsNoDuplicateJobCodes(projectJobStatusList)) {
                throw new DuplicateJobCodeException();
            }
        }

        projectRepository.updateProjectJobs(projectCode, projectJobStatusList);
    }
}
