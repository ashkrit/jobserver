package jobserver.server.service;

import jobserver.server.service.impl.DiskJobStore;
import org.springframework.core.env.Environment;

public enum JobStoreType {
    DISK {
        public JobStore create(Environment environment) {
            return new DiskJobStore(environment);
        }
    };

    public JobStore create(Environment environment) {
        throw new IllegalArgumentException("Not supported");
    }
}
