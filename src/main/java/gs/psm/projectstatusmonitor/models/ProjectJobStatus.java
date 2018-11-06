package gs.psm.projectstatusmonitor.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJobStatus {
    private String jobName;
    private JobStatus jobStatus;
}
