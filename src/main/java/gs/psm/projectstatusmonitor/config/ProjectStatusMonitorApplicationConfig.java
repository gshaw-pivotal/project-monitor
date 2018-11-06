package gs.psm.projectstatusmonitor.config;

import gs.psm.projectstatusmonitor.ports.ProjectRepository;
import gs.psm.projectstatusmonitor.repositories.InMemoryProjectProjectRepository;
import gs.psm.projectstatusmonitor.usecases.ProjectUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectStatusMonitorApplicationConfig {

    @Bean
    public ProjectRepository projectRepository() {
        return new InMemoryProjectProjectRepository();
    }

    @Bean
    public ProjectUseCase projectUseCase(
            ProjectRepository projectRepository) {
        return new ProjectUseCase(projectRepository);
    }
}
