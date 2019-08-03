package jobserver.server.protocol.job;

import java.util.Map;

public class Subscription {
    private SubscriptionType type;
    private Map<String, String> properties;

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
