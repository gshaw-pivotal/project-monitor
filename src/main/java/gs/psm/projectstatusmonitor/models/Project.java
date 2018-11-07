package gs.psm.projectstatusmonitor.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @NotNull
    private String projectCode;

    @NotNull
    private String projectName;

    @Valid
    private List<ProjectJobStatus> jobStatusList;
}
