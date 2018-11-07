package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.usecases.StatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @Autowired
    private StatusUseCase statusUseCase;

    @GetMapping(value = "/status/{projectCode}")
    public ResponseEntity getProjectJobStatus(@PathVariable String projectCode) {
        return new ResponseEntity(statusUseCase.getJobStatus(projectCode), HttpStatus.OK);
    }
}
