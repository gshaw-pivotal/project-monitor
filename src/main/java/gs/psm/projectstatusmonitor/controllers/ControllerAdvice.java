package gs.psm.projectstatusmonitor.controllers;

import gs.psm.projectstatusmonitor.exceptions.DeleteProjectException;
import gs.psm.projectstatusmonitor.exceptions.ProjectAlreadyExistsException;
import gs.psm.projectstatusmonitor.exceptions.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ProjectAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    void handleProjectAlreadyExistsException() {}

    @ExceptionHandler(value = ProjectNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    void handleProjectnotFoundException() {}

    @ExceptionHandler(value = DeleteProjectException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    void handleDeleteProjectException() {}
}
