package jobserver.server.resource;

import jobserver.server.protocol.job.AllJobsResponse;
import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.JobStagesResponse;
import jobserver.server.protocol.job.RegisterJobInfo;
import jobserver.server.service.JobStages;
import jobserver.server.service.JobStoreService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class JobResource {
    private static Logger logger = getLogger(JobResource.class);

    @Autowired
    Environment environment;

    @Autowired
    JobStoreService jobStore;

    @GetMapping("/")
    String welcome() {
        return String.format("You are connected to Job server %s", instanceName());
    }

    @GetMapping("/jobs")
    AllJobsResponse allJobs() {
        return AllJobsResponse.create(jobStore.jobs());
    }

    @GetMapping("/jobs/stage/{id}")
    JobStagesResponse jobStageById(@PathVariable String id) {

        logger.info("Received request for job {} ", id);
        RegisterJobInfo job = jobStore.job(id);
        JobStages stages = jobStore.stages(id);

        return JobStagesResponse.create(job, stages.getStages());

    }

    @PostMapping("/jobs/registerJob")
    void registerJob(@RequestBody RegisterJobInfo newJobRequest) {

        logger.info("Received request for job creation {}", newJobRequest.getJobId());
        jobStore.registerJob(newJobRequest);

    }

    @PostMapping("/jobs/stage")
    void onStageUpdate(@RequestBody JobStageInfo newStageInfo) {

        logger.info("Received request for job {} stage {} ", newStageInfo.getJobId(), newStageInfo.getStageName());
        jobStore.recordStage(newStageInfo);
    }


    private String instanceName() {
        return environment.getProperty("jobserver.instance.name");
    }
}