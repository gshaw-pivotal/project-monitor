package gs.psm.projectstatusmonitor.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJobStatusList {

    @Valid
    private List<ProjectJobStatus> projectJobStatusList;
}
