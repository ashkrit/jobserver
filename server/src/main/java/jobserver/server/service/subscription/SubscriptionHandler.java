package jobserver.server.service.subscription;

import jobserver.server.lang.RaiseExceptions;
import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.RegisterJobInfo;
import jobserver.server.protocol.job.Subscription;
import jobserver.server.protocol.job.SubscriptionType;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class SubscriptionHandler {

    private static Logger logger = getLogger(SubscriptionHandler.class);

    public void notifySubscriber(RegisterJobInfo job, JobStageInfo stage) {

        CompletableFuture<Void> completableFuture = supplyAsync(() -> {
            logger.info("Sending notification for job {}", job.getJobId());
            List<Subscription> listeners = job.getSubscriptions();
            listeners.forEach(endPoint -> handleSingleSubscription(stage, endPoint));
            return null;
        }).thenAccept((x) -> {
            logger.info("Notification for job {} , stage {} completed", job.getJobId(), stage.getStageName());
        });

        completableFuture.exceptionally(x -> {
            String errorMessage = String.format("Error in handling Job %s , Stage %s", job.getJobId(), stage.getStageName());
            logger.error(errorMessage, x);
            if (job.getNotification() != null) {

            }
            return null;
        });
    }

    private void handleSingleSubscription(JobStageInfo stage, Subscription endPoint) {
        SubscriptionType clientType = endPoint.getType();
        if (clientType.equals(SubscriptionType.REST)) {
            handleRestClient(stage, endPoint);
        }
    }

    private void handleRestClient(JobStageInfo stage, Subscription endPoint) {
        try {
            String restEndpoint = endPoint.getProperties().get("endpoint");
            RestTemplate template = new RestTemplate();
            String reply = template.postForObject(restEndpoint, stage, String.class);
            logger.info("Reply from {} is {}", restEndpoint, reply);
        } catch (Exception e) {
            RaiseExceptions.raise(e);
        }
    }


}
