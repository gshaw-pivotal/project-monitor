package gs.psm.projectstatusmonitor.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
public class AddProjectResponse {

    @NotNull
    private String projectCode;

    @NotNull
    private String projectName;

    private boolean addSuccess;
    private String message;
}
