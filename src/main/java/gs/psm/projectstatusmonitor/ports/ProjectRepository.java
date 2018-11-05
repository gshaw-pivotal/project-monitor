package gs.psm.projectstatusmonitor.ports;

import gs.psm.projectstatusmonitor.models.Project;

import java.util.List;

public interface ProjectRepository {

    Project addProject(Project addProject);

    List<Project> listProjects();
}
