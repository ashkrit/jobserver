package jobserver.client.protocol;

import java.time.LocalDateTime;
import java.util.List;

public class RegisterJobInfo {

    private String jobId;
    private String jobName;
    private String category;
    private List<Subscription> subscriptions;
    private LocalDateTime timestamp;
    private NotificationInfo notification;

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setNotification(NotificationInfo notification) {
        this.notification = notification;
    }

    public NotificationInfo getNotification() {
        return notification;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
}
