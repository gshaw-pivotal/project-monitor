package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.models.ProjectJobStatus;
import gs.psm.projectstatusmonitor.models.ProjectJobStatusList;
import gs.psm.projectstatusmonitor.usecases.StatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class StatusController {

    @Autowired
    private StatusUseCase statusUseCase;

    @GetMapping(value = "/status/{projectCode}")
    public ResponseEntity getProjectJobStatus(@PathVariable String projectCode) {
        return new ResponseEntity(statusUseCase.getJobStatus(projectCode), HttpStatus.OK);
    }

    @PostMapping(value = "/status/updateList/{projectCode}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity updateProjectJobStatusList(
            @PathVariable String projectCode,
            @Validated @RequestBody ProjectJobStatusList projectJobStatusList
    ) {
        statusUseCase.updateProjectJobs(projectCode, projectJobStatusList.getProjectJobStatusList());
        return new ResponseEntity(HttpStatus.OK);
    }
}
