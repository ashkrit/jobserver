package jobserver.client.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobsResponse {

    private List<RegisterJobInfo> jobs;

    public void setJobs(List<RegisterJobInfo> jobs) {
        this.jobs = jobs;
    }

    public List<RegisterJobInfo> getJobs() {
        return jobs;
    }

}
