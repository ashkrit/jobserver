package jobserver.server.service;

import jobserver.server.service.impl.DiskJobStore;
import jobserver.server.service.subscription.SubscriptionHandler;
import org.springframework.core.env.Environment;

public enum JobStoreType {
    DISK {
        public JobStore create(Environment environment, SubscriptionHandler handler) {
            return new DiskJobStore(environment, handler);
        }
    };

    public JobStore create(Environment environment, SubscriptionHandler handler) {
        throw new IllegalArgumentException("Not supported");
    }
}
