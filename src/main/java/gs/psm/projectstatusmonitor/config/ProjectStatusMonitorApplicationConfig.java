package gs.psm.projectstatusmonitor.config;

import gs.psm.projectstatusmonitor.ports.ProjectRepository;
import gs.psm.projectstatusmonitor.repositories.InMemoryProjectProjectRepository;
import gs.psm.projectstatusmonitor.usecases.ProjectJobStatusHelper;
import gs.psm.projectstatusmonitor.usecases.ProjectUseCase;
import gs.psm.projectstatusmonitor.usecases.StatusUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectStatusMonitorApplicationConfig {

    @Bean
    public ProjectJobStatusHelper projectJobStatusHelper() {
        return new ProjectJobStatusHelper();
    }

    @Bean
    public ProjectRepository projectRepository() {
        return new InMemoryProjectProjectRepository();
    }

    @Bean
    public ProjectUseCase projectUseCase(
            ProjectRepository projectRepository,
            ProjectJobStatusHelper projectJobStatusHelper
            ) {
        return new ProjectUseCase(projectRepository, projectJobStatusHelper);
    }

    @Bean
    public StatusUseCase statusUseCase(
            ProjectRepository projectRepository,
            ProjectJobStatusHelper projectJobStatusHelper
    ) {
        return new StatusUseCase(projectRepository,projectJobStatusHelper);
    }
}
