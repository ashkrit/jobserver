package jobserver.server.resource;

import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.RegisterJobInfo;
import jobserver.server.service.JobStoreService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/jobs/registerJob")
    void registerJob(@RequestBody RegisterJobInfo newJobRequest) {

        logger.info("Received request for job creation {}", newJobRequest.getJobId());
        jobStore.registerJob(newJobRequest);

    }

    @PostMapping("/jobs/stage")
    void registerJob(@RequestBody JobStageInfo newStageInfo) {

        logger.info("Received request for job {} stage {} ", newStageInfo.getJobId(), newStageInfo.getStageName());
        jobStore.recordStage(newStageInfo);

    }


    private String instanceName() {
        return environment.getProperty("jobserver.instance.name");
    }
}