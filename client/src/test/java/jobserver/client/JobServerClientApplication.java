package jobserver.client;

import jobserver.client.protocol.JobStageInfo;
import jobserver.client.protocol.RegisterJobInfo;
import jobserver.client.protocol.Subscription;
import jobserver.client.protocol.SubscriptionType;

import java.util.ArrayList;
import java.util.HashMap;

public class JobServerClientApplication {

    public static void main(String... args) throws Exception {

        JobServerClient client = JobServerClient.defaultClient("http://localhost:8080/");

        String jobId = "20190804_FXUPDATE_RUN3";
        String value = "http://localhost:8080/jobserver/callback/stage";

        RegisterJobInfo job = createNewJob(jobId, value);
        client.registerJob(job);

        JobStageInfo stage = new JobStageInfo();
        stage.setJobId(jobId);
        stage.setStageName("LoadFXRate");
        stage.setEventName("European-Central-Bank-Started");

        client.stageUpdate(stage);

    }

    private static RegisterJobInfo createNewJob(String jobId, String value) {
        RegisterJobInfo job = new RegisterJobInfo();
        job.setJobId(jobId);
        job.setCategory("test");
        job.setJobName("This is Random job");
        job.setSubscriptions(new ArrayList<>());
        Subscription s = new Subscription();
        s.setType(SubscriptionType.REST);
        s.setProperties(new HashMap<>());
        s.getProperties().put("endpoint", value);

        job.getSubscriptions().add(s);
        return job;
    }

}
