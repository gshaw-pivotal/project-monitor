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
public class Project {

    @NotNull
    private String projectCode;
    @NotNull
    private String projectName;
}
