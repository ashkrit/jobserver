package jobserver.server.resource;

import jobserver.server.json.JsonConverter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class CallBackResource {
    private static Logger logger = getLogger(CallBackResource.class);

    @Autowired
    Environment environment;

    @PostMapping("/callback/stage")
    void onStageMessage(@RequestBody Map<String, Object> stageMessage) {
        logger.info("Received Stage update message {}", JsonConverter.toJson(stageMessage));
    }

}