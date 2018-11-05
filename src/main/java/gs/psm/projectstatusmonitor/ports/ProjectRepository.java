package gs.psm.projectstatusmonitor.ports;

import gs.psm.projectstatusmonitor.models.Project;

public interface ProjectRepository {

    Project addProject(Project addProject);
}
