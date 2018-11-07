package gs.psm.projectstatusmonitor.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJobStatus {

    @NotNull
    private String jobCode;

    private String jobName;

    private JobStatus jobStatus;
}
