package jobserver.client.protocol;

public class NotificationInfo {
    private String to;
    private String subject;

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public String getTo() {
        return to;
    }
}
