package jobserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableAutoConfiguration
@PropertySource("classpath:jobserver.properties")
public class JobServerApplication {

    private static Logger logger = LoggerFactory.getLogger(JobServerApplication.class);

    public static void main(String[] args) {
        logger.info("Starting....");
        SpringApplication.run(JobServerApplication.class, args);
        logger.info("Started");
    }
}
