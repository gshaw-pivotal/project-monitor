package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.exceptions.DeleteProjectException;
import gs.psm.projectstatusmonitor.exceptions.DuplicateJobCodeException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.exceptions.UserActionNotAllowedException;
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

    public void addProject(Project project, String username) {

        List<ProjectJobStatus> jobs = project.getJobStatusList();

        if (jobs != null && jobs.size() > 0) {
            if (!projectJobStatusHelper.containsNoDuplicateJobCodes(jobs)) {
                throw new DuplicateJobCodeException();
            }
        }

        projectRepository.addProject(project);
        projectRepository.associateUserWithProject(username, project.getProjectCode());
    }

    public List<Project> listProjects() {
        return projectRepository.listProjects();
    }

    public Project getProject(String projectCode,String username) {
        if (isUserAssociatedWithProjectCode(username, projectCode)) {
            Project foundProject = projectRepository.getProject(projectCode);
            if (foundProject != null) {
                return foundProject;
            }

            throw new ProjectNotFoundException();
        }

        throw new UserActionNotAllowedException();
    }

    public void removeProject(String projectCodeToDelete, String username) {
        if (isUserAssociatedWithProjectCode(username, projectCodeToDelete)) {

            if (!projectRepository.removeProject(projectCodeToDelete)) {
                throw new DeleteProjectException();
            }

            return;
        }

        throw new UserActionNotAllowedException();
    }

    public void updateProject(Project project, String username) {
        if (isUserAssociatedWithProjectCode(username, project.getProjectCode())) {

            List<ProjectJobStatus> jobs = project.getJobStatusList();

            if (jobs != null && jobs.size() > 0) {
                if (!projectJobStatusHelper.containsNoDuplicateJobCodes(jobs)) {
                    throw new DuplicateJobCodeException();
                }
            }

            projectRepository.updateProject(project);
            return;
        }

        throw new UserActionNotAllowedException();
    }

    private boolean isUserAssociatedWithProjectCode(String username, String projectCode) {
        List<String> associatedProjectCodes = projectRepository.getUserAssociatedProjectCodes(username);

        return associatedProjectCodes.contains(projectCode);
    }
}
