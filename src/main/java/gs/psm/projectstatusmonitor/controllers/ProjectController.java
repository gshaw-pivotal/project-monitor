package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.models.Project;
import gs.psm.projectstatusmonitor.usecases.ProjectUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class ProjectController {

    @Autowired
    private ProjectUseCase projectUseCase;

    @PostMapping(value = "/project/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addProject(@Validated @RequestBody Project request,
                                     @RequestHeader(value="Authorization") String authHeader) {
        projectUseCase.addProject(request, decodeUsername(authHeader));
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping(value = "/project/update", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity updateProject(@Validated @RequestBody Project request,
                                        @RequestHeader(value="Authorization") String authHeader) {
        projectUseCase.updateProject(request, decodeUsername(authHeader));
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/project/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity listProjects() {
        return new ResponseEntity(projectUseCase.listProjects(), HttpStatus.OK);
    }

    @GetMapping(value = "/project/{projectCode}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getProject(@PathVariable String projectCode,
                                     @RequestHeader(value="Authorization") String authHeader) {
        return new ResponseEntity(projectUseCase.getProject(projectCode, decodeUsername(authHeader)), HttpStatus.OK);
    }

    @DeleteMapping(value = "/project/{projectCode}")
    public ResponseEntity removeProject(@PathVariable String projectCode,
                                        @RequestHeader(value="Authorization") String authHeader) {
        projectUseCase.removeProject(projectCode, decodeUsername(authHeader));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private String decodeUsername(String authHeader) {
        return new String(Base64Utils.decode(authHeader.split(" ")[1].getBytes())).split(":")[0];
    }
}
