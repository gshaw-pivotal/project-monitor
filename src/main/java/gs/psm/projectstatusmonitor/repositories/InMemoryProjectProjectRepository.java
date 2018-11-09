package gs.psm.projectstatusmonitor.repositories;

import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.exceptions.ProjectJobStatusNotFoundException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.ports.ProjectRepository;

import javax.validation.Valid;
import java.util.*;

public class InMemoryProjectProjectRepository implements ProjectRepository {

    private Map<String, Project> projectRecords = new HashMap<>();

    @Override
    public Project addProject(Project addProject) {

        Project record = projectRecords.get(addProject.getProjectCode());
        if (record != null) {
            throw new ProjectAlreadyExistsException();
        }

        projectRecords.put(addProject.getProjectCode(), addProject);
        return addProject;
    }

    @Override
    public List<Project> listProjects() {
        return new ArrayList<>(projectRecords.values());
    }

    @Override
    public Project getProject(String projectCode) {
        return projectRecords.get(projectCode);
    }

    @Override
    public boolean removeProject(String projectCode) {
        Project deletedProject = projectRecords.remove(projectCode);
        if (deletedProject != null) {
            return true;
        }

        throw new ProjectNotFoundException();
    }

    @Override
    public Project updateProject(Project updateProject) {
        Project oldProject = projectRecords.replace(updateProject.getProjectCode(), updateProject);
        if (oldProject != null) {
            return updateProject;
        }

        throw new ProjectNotFoundException();
    }

    @Override
    public Project updateProjectJobs(String projectCode, List<ProjectJobStatus> projectJobStatusList) {
        Project project = projectRecords.get(projectCode);

        if (project != null) {
            project.setJobStatusList(projectJobStatusList);
            return project;
        }

        throw new ProjectNotFoundException();
    }

    @Override
    public Project updateJob(String projectCode, String jobCode, ProjectJobStatus projectJobStatus) {
        Project existingProject = projectRecords.get(projectCode);

        if (existingProject != null) {

            List<ProjectJobStatus> existingProjectJobStatusList = existingProject.getJobStatusList();

            if (existingProjectJobStatusList != null && existingProjectJobStatusList.size() > 0) {

                Optional<ProjectJobStatus> maybeExistingJobStatus = existingProjectJobStatusList
                        .stream()
                        .filter(jobStatus -> jobStatus.getJobCode().equals(jobCode))
                        .findFirst();

                if (maybeExistingJobStatus.isPresent()) {
                    ProjectJobStatus existingProjectJobStatus = maybeExistingJobStatus.get();

                    existingProjectJobStatus.setJobName(projectJobStatus.getJobName());
                    existingProjectJobStatus.setJobStatus(projectJobStatus.getJobStatus());

                    return existingProject;
                }
            }

            throw new ProjectJobStatusNotFoundException();
        }

        throw new ProjectNotFoundException();
    }
}
