package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.exceptions.DeleteProjectException;
import gs.psm.projectstatusmonitor.exceptions.DuplicateJobCodeException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.ports.ProjectRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectUseCase {

    private ProjectRepository projectRepository;

    private ProjectJobStatusHelper projectJobStatusHelper;

    public ProjectUseCase(
            ProjectRepository projectRepository,
            ProjectJobStatusHelper projectJobStatusHelper
    ) {
        this.projectRepository = projectRepository;
        this.projectJobStatusHelper = projectJobStatusHelper;
    }

    public void addProject(Project project) {

        List<ProjectJobStatus> jobs = project.getJobStatusList();

        if (jobs != null && jobs.size() > 0) {
            if (!projectJobStatusHelper.containsNoDuplicateJobCodes(jobs)) {
                throw new DuplicateJobCodeException();
            }
        }

        projectRepository.addProject(project);
    }

    public List<Project> listProjects() {
        return projectRepository.listProjects();
    }

    public Project getProject(String projectCode) {
        Project foundProject = projectRepository.getProject(projectCode);
        if (foundProject != null) {
            return foundProject;
        }

        throw new ProjectNotFoundException();
    }

    public void removeProject(String projectCodeToDelete) {
        if (!projectRepository.removeProject(projectCodeToDelete)) {
            throw new DeleteProjectException();
        }
    }

    public void updateProject(Project project) {

        List<ProjectJobStatus> jobs = project.getJobStatusList();

        if (jobs != null && jobs.size() > 0) {
            if (!projectJobStatusHelper.containsNoDuplicateJobCodes(jobs)) {
                throw new DuplicateJobCodeException();
            }
        }

        projectRepository.updateProject(project);
    }
}
