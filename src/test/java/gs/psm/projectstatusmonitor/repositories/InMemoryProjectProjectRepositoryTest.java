package gs.psm.projectstatusmonitor.repositories;

import gs.psm.projectstatusmonitor.ports.ProjectRepository;
import gs.psm.projectstatusmonitor.ports.ProjectRepositoryTest;

public class InMemoryProjectProjectRepositoryTest extends ProjectRepositoryTest {

    @Override
    public ProjectRepository createInstance() {
        return new InMemoryProjectProjectRepository();
    }
}