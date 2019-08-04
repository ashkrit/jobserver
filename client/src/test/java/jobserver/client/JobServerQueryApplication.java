package jobserver.client;

import jobserver.client.protocol.AllJobsResponse;
import jobserver.client.protocol.JobStagesResponse;
import jobserver.client.protocol.RegisterJobInfo;

public class JobServerQueryApplication {

    public static void main(String... args) throws Exception {

        JobServerClient client = JobServerClient.create("http://localhost:8080/");
        AllJobsResponse jobs = client.jobs();

        System.out.println(jobs);

        RegisterJobInfo job = jobs.getJobs().get(0);
        JobStagesResponse stages = client.jobStages(job.getJobId());

        System.out.println(stages);

    }


}
