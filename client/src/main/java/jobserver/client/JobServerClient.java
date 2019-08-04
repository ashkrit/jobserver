package jobserver.client;

import jobserver.client.impl.SimpleHTTPJobServerClient;
import jobserver.client.protocol.JobStageInfo;
import jobserver.client.protocol.JobStagesResponse;
import jobserver.client.protocol.JobsResponse;
import jobserver.client.protocol.RegisterJobInfo;

public interface JobServerClient {

    void registerJob(RegisterJobInfo jobInfo) throws Exception;

    void stageUpdate(JobStageInfo stageInfo) throws Exception;

    JobsResponse jobs() throws Exception;

    JobStagesResponse jobStages(String id) throws Exception;

    static JobServerClient defaultClient(String server) {
        return new SimpleHTTPJobServerClient(server);
    }

}
