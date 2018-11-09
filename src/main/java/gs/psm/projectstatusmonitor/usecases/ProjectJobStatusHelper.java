package gs.psm.projectstatusmonitor.usecases;

import gs.psm.projectstatusmonitor.models.ProjectJobStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProjectJobStatusHelper {
    public Set<String> getUniqueJobCodes(List<ProjectJobStatus> projectJobStatusList) {
        Set<String> uniqueJobCodes = new HashSet<>();

        projectJobStatusList.stream().forEach(job -> uniqueJobCodes.add(job.getJobCode()));
        return uniqueJobCodes;
    }

    public boolean containsNoDuplicateJobCodes(List<ProjectJobStatus> projectJobStatusList) {
        Set<String> uniqueJobCodes = getUniqueJobCodes(projectJobStatusList);
        if (uniqueJobCodes.size() == projectJobStatusList.size()) {
            return true;
        }

        return false;
    }
}
