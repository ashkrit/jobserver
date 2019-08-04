package jobserver.client;

import jobserver.client.json.JsonConverter;
import jobserver.client.protocol.JobStagesResponse;
import jobserver.client.protocol.JobsResponse;
import jobserver.client.protocol.RegisterJobInfo;

public class JobServerQueryApplication {

    public static void main(String... args) throws Exception {

        JobServerClient client = JobServerClient.create("http://localhost:8080/");
        JobsResponse jobs = client.jobs();

        System.out.println(JsonConverter.toJson(jobs));

        RegisterJobInfo job = jobs.getJobs().get(0);
        JobStagesResponse stages = client.jobStages(job.getJobId());

        System.out.println(JsonConverter.toJson(stages));

    }


}
