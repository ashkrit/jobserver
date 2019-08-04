package jobserver.client.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobStagesResponse {

    private RegisterJobInfo job;
    private List<JobStageInfo> stages;

    public void setStages(List<JobStageInfo> stages) {
        this.stages = stages;
    }

    public List<JobStageInfo> getStages() {
        return stages;
    }

    public RegisterJobInfo getJob() {
        return job;
    }

    public void setJob(RegisterJobInfo job) {
        this.job = job;
    }

}
