package gs.psm.projectstatusmonitor.repositories;

import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.exceptions.ProjectJobStatusNotFoundException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import gs.psm.projectstatusmonitor.models.JobStatus;
import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryProjectProjectRepositoryTest {

    private InMemoryProjectProjectRepository repository;

    private List<Project> expectedProjectList;

    @Before
    public void setup() {
        repository = new InMemoryProjectProjectRepository();

        expectedProjectList = new ArrayList<>();
    }

    @Test
    public void addProject_forANewProject_addsTheProject() {
        Project newProject = Project.builder()
                .projectCode("code")
                .projectName("name")
                .build();

        Project responseProject = repository.addProject(newProject);

        assertThat(responseProject).isNotNull();
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void addProject_forADuplicateProject_throwsProjectAlreadyExistsException() {
        Project newProject = Project.builder()
                .projectCode("code")
                .projectName("name")
                .build();

        repository.addProject(newProject);
        repository.addProject(newProject);
    }

    @Test
    public void listProjects_whenThereAreNoProjects_returnsAnEmptyList() {
        List<Project> projects = repository.listProjects();

        assertThat(projects.size()).isEqualTo(0);
    }

    @Test
    public void listProjects_whenThereIsOneProject_returnsAListWithJustThatProject() {
        Project newProject = Project.builder()
                .projectCode("code")
                .projectName("name")
                .build();

        repository.addProject(newProject);

        List<Project> projectList = repository.listProjects();

        assertThat(projectList.size()).isEqualTo(1);

        Project project = projectList.get(0);

        assertThat(project.getProjectCode()).isEqualTo(newProject.getProjectCode());
        assertThat(project.getProjectName()).isEqualTo(newProject.getProjectName());
    }

    @Test
    public void listProjects_whenThereAreMultipleProjects_returnsAListWithAllTheProjects() {
        int numberOfProjects = 5;

        for (int count = 0; count < numberOfProjects; count++) {
            addProjectToRepository(count);
        }

        List<Project> projectList = repository.listProjects();

        assertThat(projectList.size()).isEqualTo(numberOfProjects);
        assertThat(projectList.containsAll(expectedProjectList));
    }

    @Test
    public void getProject_givenAProjectCodeThatExists_returnsTheCorrespondingProject() {
        String projectCode = "code1";
        addProjectToRepository(1);

        Project returnedProject = repository.getProject(projectCode);

        assertThat(returnedProject.getProjectCode()).isEqualTo(projectCode);
        assertThat(returnedProject.getProjectName()).isEqualTo("name1");
    }

    @Test
    public void getProject_givenAProjectCodeThatDoesNotExist_returnsNull() {
        Project returnedProject = repository.getProject("notExisting");

        assertThat(returnedProject).isNull();
    }

    @Test
    public void removeProject_givenAProjectCodeThatExists_returnsTrue() {
        String projectCode = "code1";
        addProjectToRepository(1);

        boolean deleted = repository.removeProject(projectCode);

        assertThat(deleted).isTrue();
    }

    @Test(expected = ProjectNotFoundException.class)
    public void removeProject_givenAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        addProjectToRepository(1);

        repository.removeProject("codeNotFound");
    }

    @Test
    public void updateProject_givenAProject_withAProjectCodeThatExists_updatesTheProject() {
        addProjectToRepository(1);

        Project updatedProject = Project.builder()
                .projectCode("code1")
                .projectName("newName")
                .build();

        Project returnedProject = repository.updateProject(updatedProject);

        assertThat(returnedProject.getProjectCode()).isEqualTo(updatedProject.getProjectCode());
        assertThat(returnedProject.getProjectName()).isEqualTo(updatedProject.getProjectName());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void updateProject_givenAProject_withAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        Project updatedProject = Project.builder()
                .projectCode("code1")
                .projectName("newName")
                .build();

        repository.updateProject(updatedProject);
    }

    @Test
    public void updateProjectJobs_givenAValidProjectCodeThatExists_andAListOfProjectJobStatus_updatesTheJobsOfTheProject() {
        String projectCode = "code1";

        List<ProjectJobStatus> oldProjectJobStatusList = new ArrayList<>();
        oldProjectJobStatusList.add(createJobStatus("old-code-1", "old-name-1", JobStatus.PASSED));
        oldProjectJobStatusList.add(createJobStatus("old-code-2", "old-name-2", JobStatus.PASSED));
        oldProjectJobStatusList.add(createJobStatus("old-code-3", "old-name-3", JobStatus.PASSED));

        addProjectToRepository(1, oldProjectJobStatusList);

        List<ProjectJobStatus> newProjectJobStatusList = new ArrayList<>();
        newProjectJobStatusList.add(createJobStatus("new-code-1", "new-name-1", JobStatus.FAILED));
        newProjectJobStatusList.add(createJobStatus("new-code-2", "new-name-2", JobStatus.FAILED));
        newProjectJobStatusList.add(createJobStatus("new-code-3", "new-name-3", JobStatus.FAILED));

        Project returnedProject = repository.updateProjectJobs(projectCode, newProjectJobStatusList);

        assertThat(returnedProject.getJobStatusList().containsAll(newProjectJobStatusList));

        assertThat(repository.getProject(projectCode).getJobStatusList().containsAll(newProjectJobStatusList));
    }

    @Test
    public void updateProjectJobs_givenAValidProjectCodeThatExists_andAnEmptyListOfProjectJobStatus_updateTheJobsOfTheProjectToBeEmpty() {
        String projectCode = "code1";

        List<ProjectJobStatus> oldProjectJobStatusList = new ArrayList<>();
        oldProjectJobStatusList.add(createJobStatus("old-code-1", "old-name-1", JobStatus.PASSED));
        oldProjectJobStatusList.add(createJobStatus("old-code-2", "old-name-2", JobStatus.PASSED));
        oldProjectJobStatusList.add(createJobStatus("old-code-3", "old-name-3", JobStatus.PASSED));

        addProjectToRepository(1, oldProjectJobStatusList);

        Project returnedProject = repository.updateProjectJobs(projectCode, Collections.emptyList());

        assertThat(returnedProject.getJobStatusList().size()).isEqualTo(0);

        assertThat(repository.getProject(projectCode).getJobStatusList().size()).isEqualTo(0);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void updateProjectJobs_givenAProjectCodeThatDoesExists_throwsProjectNotFoundException() {
        List<ProjectJobStatus> newProjectJobStatusList = new ArrayList<>();
        newProjectJobStatusList.add(createJobStatus("new-code-1", "new-name-1", JobStatus.FAILED));
        newProjectJobStatusList.add(createJobStatus("new-code-2", "new-name-2", JobStatus.FAILED));
        newProjectJobStatusList.add(createJobStatus("new-code-3", "new-name-3", JobStatus.FAILED));

        repository.updateProjectJobs("code-does-not-exist", newProjectJobStatusList);
    }

    @Test
    public void updateJob_givenAProjectCodeThatExists_andAJobCodeThatExistsForThatProjectCode_updatesThatSingleJob() {
        List<ProjectJobStatus> initialProjectJobStatusList = new ArrayList<>();

        initialProjectJobStatusList.add(createJobStatus("code-1", "name-1", JobStatus.PASSED));
        initialProjectJobStatusList.add(createJobStatus("code-2", "name-2", JobStatus.PASSED));
        initialProjectJobStatusList.add(createJobStatus("code-3", "name-3", JobStatus.PASSED));

        addProjectToRepository(1, initialProjectJobStatusList);

        ProjectJobStatus updatedProjectJobStatus = createJobStatus("code-1", "new-name-1", JobStatus.FAILED);

        List<ProjectJobStatus> expectedProjectJobStatusList = Arrays.asList(
                updatedProjectJobStatus,
                createJobStatus("code-2", "name-2", JobStatus.PASSED),
                createJobStatus("code-3", "name-3", JobStatus.PASSED)
        );

        Project updatedProject = repository.updateJob("code1", "code-1", updatedProjectJobStatus);

        assertThat(updatedProject.getJobStatusList().containsAll(expectedProjectJobStatusList)).isTrue();

        assertThat(repository.getProject("code1").getJobStatusList().containsAll(expectedProjectJobStatusList)).isTrue();
    }

    @Test(expected = ProjectNotFoundException.class)
    public void updateJob_givenAProjectCodeThatDoesNotExist_throwsProjectNotFoundException() {
        ProjectJobStatus updatedProjectJobStatus = createJobStatus("code-1", "new-name-1", JobStatus.FAILED);

        repository.updateJob("code1", "code-1", updatedProjectJobStatus);
    }

    @Test(expected = ProjectJobStatusNotFoundException.class)
    public void updateJob_givenAProjectCodeThatExists_andTheProjectHasNoProjectJobStatusList_throwsProjectJobStatusNotFoundException() {
        addProjectToRepository(1);

        ProjectJobStatus updatedProjectJobStatus = createJobStatus("code-1", "new-name-1", JobStatus.FAILED);

        repository.updateJob("code1", "code-1", updatedProjectJobStatus);
    }

    @Test(expected = ProjectJobStatusNotFoundException.class)
    public void updateJob_givenAProjectCodeThatExists_andTheProjectHasAnEmptyProjectJobStatusList_throwsProjectJobStatusNotFoundException() {
        addProjectToRepository(1, Collections.emptyList());

        ProjectJobStatus updatedProjectJobStatus = createJobStatus("code-1", "new-name-1", JobStatus.FAILED);

        repository.updateJob("code1", "code-1", updatedProjectJobStatus);
    }

    @Test(expected = ProjectJobStatusNotFoundException.class)
    public void updateJob_givenAProjectCodeThatExists_andHasAProjectJobStatusList_andAJobCodeThatDoesNotExist_throwsProjectJobStatusNotFoundException() {
        List<ProjectJobStatus> initialProjectJobStatusList = new ArrayList<>();

        initialProjectJobStatusList.add(createJobStatus("code-1", "name-1", JobStatus.PASSED));

        addProjectToRepository(1, initialProjectJobStatusList);

        ProjectJobStatus updatedProjectJobStatus = createJobStatus("code-5", "new-name-1", JobStatus.FAILED);

        repository.updateJob("code1", "code-5", updatedProjectJobStatus);
    }

    private void addProjectToRepository(int increment) {
        Project newProject = Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .build();

        repository.addProject(newProject);

        expectedProjectList.add(newProject);
    }

    private void addProjectToRepository(int increment, List<ProjectJobStatus> jobStatusList) {
        Project newProject = Project.builder()
                .projectCode("code" + increment)
                .projectName("name" + increment)
                .jobStatusList(jobStatusList)
                .build();

        repository.addProject(newProject);

        expectedProjectList.add(newProject);
    }

    private ProjectJobStatus createJobStatus(String code, String name, JobStatus status) {
        return ProjectJobStatus.builder()
                .jobCode(code)
                .jobName(name)
                .jobStatus(status)
                .build();
    }
}