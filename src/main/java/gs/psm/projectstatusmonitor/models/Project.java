package gs.psm.projectstatusmonitor.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Project {

    private String projectCode;
    private String projectName;
}
