package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.usecases.ProjectUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProjectController {

    @Autowired
    private ProjectUseCase projectUseCase;

    @PostMapping(value = "/project/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addProject(@Validated @RequestBody Project request) {
        projectUseCase.addProject(request, "");
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping(value = "/project/update", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity updateProject(@Validated @RequestBody Project request) {
        projectUseCase.updateProject(request);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/project/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity listProjects() {
        return new ResponseEntity(projectUseCase.listProjects(), HttpStatus.OK);
    }

    @GetMapping(value = "/project/{projectCode}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getProject(@PathVariable String projectCode) {
        return new ResponseEntity(projectUseCase.getProject(projectCode), HttpStatus.OK);
    }

    @DeleteMapping(value = "/project/{projectCode}")
    public ResponseEntity removeProject(@PathVariable String projectCode) {
        projectUseCase.removeProject(projectCode);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
