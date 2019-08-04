package jobserver.server.service;

import jobserver.server.protocol.job.JobStageInfo;

import java.util.ArrayList;
import java.util.List;

public class JobStages {

    private List<JobStageInfo> stages = new ArrayList<>();

    public void setStages(List<JobStageInfo> stages) {
        this.stages = stages;
    }

    public List<JobStageInfo> getStages() {
        return stages;
    }
}
