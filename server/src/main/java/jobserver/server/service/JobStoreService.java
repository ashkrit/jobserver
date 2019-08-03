package jobserver.server.service;

import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.RegisterJobInfo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class JobStoreService {

    private static Logger logger = getLogger(JobStoreService.class);

    @Autowired
    Environment environment;

    private JobStore jobStore;

    @PostConstruct
    public void init() {
        JobStoreType storeType = JobStoreType.valueOf(environment.getProperty("jobserver.store.type"));
        jobStore = storeType.create(environment);
    }

    public void registerJob(RegisterJobInfo newJob) {
        jobStore.registerJob(newJob);
    }

    public void deleteJob(String jobId) {
        jobStore.deleteJob(jobId);
    }


    public void recordStage(JobStageInfo newStageInfo) {
        jobStore.recordStage(newStageInfo);
    }
}
