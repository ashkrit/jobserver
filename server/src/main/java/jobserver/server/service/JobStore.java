package jobserver.server.service;

import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.RegisterJobInfo;

import java.util.List;

public interface JobStore {

    void registerJob(RegisterJobInfo newJob);

    void recordStage(JobStageInfo newStageInfo);

    void deleteJob(String jobId);

    List<RegisterJobInfo> jobs();

    RegisterJobInfo job(String jobId);

    JobStages stages(String jobId);
}
