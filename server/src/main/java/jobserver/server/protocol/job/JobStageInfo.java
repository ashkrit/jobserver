package jobserver.server.protocol.job;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobStageInfo {

    private String jobId;
    private String stageName;
    private String eventName;
    private LocalDateTime timestamp;
    private Map<String, String> properties;

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getEventName() {
        return eventName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getJobId() {
        return jobId;
    }

    public String getStageName() {
        return stageName;
    }

}
