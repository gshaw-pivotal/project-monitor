package gs.psm.projectstatusmonitor.ports;

import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;

import java.util.List;

public interface ProjectRepository {

    Project addProject(Project addProject);

    List<Project> listProjects();

    Project getProject(String projectCode);

    boolean removeProject(String projectCode);

    Project updateProject(Project updateProject);

    Project updateProjectJobs(String projectCode, List<ProjectJobStatus> projectJobStatusList);
}
