package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.models.AddProjectRequest;
import gs.psm.projectstatusmonitor.usecases.ProjectUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProjectController {

    @Autowired
    private ProjectUseCase projectUseCase;

    @PostMapping(value = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addProject(@Validated @RequestBody AddProjectRequest request) {
        projectUseCase.addProject(request);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
