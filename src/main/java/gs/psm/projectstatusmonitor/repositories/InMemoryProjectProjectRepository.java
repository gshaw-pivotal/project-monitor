package gs.psm.projectstatusmonitor.repositories;

import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.ports.ProjectRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
