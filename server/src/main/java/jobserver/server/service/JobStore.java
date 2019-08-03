package jobserver.server.service;

import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.RegisterJobInfo;

public interface JobStore {

    void registerJob(RegisterJobInfo newJob);

    void recordStage(JobStageInfo newStageInfo);

    void deleteJob(String jobId);

}
