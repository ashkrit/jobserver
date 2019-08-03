package jobserver.server.service.impl;

import jobserver.server.json.JsonConverter;
import jobserver.server.lang.Exceptions;
import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.RegisterJobInfo;
import jobserver.server.service.JobStore;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static jobserver.server.json.JsonConverter.fromJson;
import static org.slf4j.LoggerFactory.getLogger;

public class DiskJobStore implements JobStore {

    private static final String JOB_INFO_FILE = "jobinfo.json";
    private static final String STAGE_INFO_FILE = "stageinfo.json";

    private static Logger logger = getLogger(DiskJobStore.class);

    private final Environment environment;
    private final ConcurrentHashMap<String, RegisterJobInfo> cache = new ConcurrentHashMap<>();
    private String storeLocation;

    public DiskJobStore(Environment environment) {
        this.environment = environment;
        this.storeLocation = this.environment.getProperty("jobserver.store.location");
        logger.info("Store location {}", storeLocation);

        initStorage();

    }

    private void initStorage() {
        Paths.get(storeLocation).toFile().mkdirs();
    }

    @Override
    public void registerJob(RegisterJobInfo newJob) {

        Path jobPath = Paths.get(storeLocation, newJob.getJobId());
        failIfJobExists(newJob.getJobId(), jobPath);
        RegisterJobInfo oldJob = cache.putIfAbsent(newJob.getJobId(), newJob);
        saveToDiskIfRequired(newJob, jobPath, oldJob);
    }

    private void saveToDiskIfRequired(RegisterJobInfo newJob, Path jobPath, RegisterJobInfo oldJob) {
        if (oldJob == null) {
            jobPath.toFile().mkdirs();
            newJob.setTimestamp(LocalDateTime.now());
            File jobInfoFile = new File(jobPath.toFile(), JOB_INFO_FILE);
            logger.info("Job info will be written to {}", jobInfoFile.getAbsolutePath());
            write(jobInfoFile.toPath(), () -> JsonConverter.toJson(newJob).getBytes());
        }
    }

    private void write(Path location, Supplier<byte[]> dataSupplier) {
        try {
            Files.write(location, dataSupplier.get());
        } catch (Exception e) {
            Exceptions.raise(e);
        }
    }

    @Override
    public void recordStage(JobStageInfo newStageInfo) {

        Path jobPath = Paths.get(storeLocation, newStageInfo.getJobId());
        failIfJobDoesNotExists(newStageInfo.getJobId(), jobPath);

        cache.computeIfAbsent(newStageInfo.getJobId(), key -> {
            File jobInfoFile = new File(jobPath.toFile(), JOB_INFO_FILE);
            logger.info("Job info will be read from {}", jobInfoFile.getAbsolutePath());
            byte[] data = read(jobInfoFile.toPath());
            return fromJson(data, RegisterJobInfo.class);
        });

        RegisterJobInfo job = cache.get(newStageInfo.getJobId());
        synchronized (job) {

            File stageInfoFile = new File(jobPath.toFile(), STAGE_INFO_FILE);
            JobStages stages = getOrNewJobStage(stageInfoFile);
            addNewStage(newStageInfo, stages);
            logger.info("Job stage info will be written to {}", stageInfoFile.getAbsolutePath());
            write(stageInfoFile.toPath(), () -> JsonConverter.toJson(stages).getBytes());
        }

    }

    private void addNewStage(JobStageInfo newStageInfo, JobStages stages) {
        newStageInfo.setTimestamp(LocalDateTime.now());
        stages.getStages().add(newStageInfo);
    }

    private JobStages getOrNewJobStage(File stageInfoFile) {
        if (stageInfoFile.exists()) {
            byte[] data = read(stageInfoFile.toPath());
            return fromJson(data, JobStages.class);
        }
        return new JobStages();
    }

    private byte[] read(Path location) {
        try {
            return Files.readAllBytes(location);
        } catch (Exception e) {
            Exceptions.raise(e);
        }
        return null;
    }

    private void failIfJobExists(String jobId, Path jobPath) {
        logger.info("Checking job {}", jobPath.toFile().getAbsolutePath());
        if (jobPath.toFile().exists()) {
            throw new IllegalArgumentException(String.format("Job is already registered %s", jobId));
        }
    }

    private void failIfJobDoesNotExists(String jobId, Path jobPath) {
        logger.info("Checking job {}", jobPath.toFile().getAbsolutePath());
        if (!jobPath.toFile().exists()) {
            throw new IllegalArgumentException(String.format("Job is already registered %s", jobId));
        }
    }

    @Override
    public void deleteJob(String jobId) {

    }
}
