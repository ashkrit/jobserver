package jobserver.server.service.impl;

import jobserver.server.json.JsonConverter;
import jobserver.server.lang.Panic;
import jobserver.server.protocol.job.JobStageInfo;
import jobserver.server.protocol.job.RegisterJobInfo;
import jobserver.server.service.JobStages;
import jobserver.server.service.JobStore;
import jobserver.server.service.subscription.SubscriptionHandler;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jobserver.server.json.JsonConverter.fromJson;
import static org.slf4j.LoggerFactory.getLogger;

public class DiskJobStore implements JobStore {

    private static final String JOB_INFO_FILE = "jobinfo.json";
    private static final String STAGE_INFO_FILE = "stageinfo.json";

    private static Logger logger = getLogger(DiskJobStore.class);

    private final Environment environment;
    private final SubscriptionHandler subscriptionHandler;
    private final ConcurrentHashMap<String, RegisterJobInfo> cache = new ConcurrentHashMap<>();

    private String storeLocation;

    public DiskJobStore(Environment environment, SubscriptionHandler subscriptionHandler) {
        this.environment = environment;
        this.subscriptionHandler = subscriptionHandler;
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
            Panic.raise(e);
        }
    }

    @Override
    public void recordStage(JobStageInfo newStageInfo) {

        String jobId = newStageInfo.getJobId();
        Path jobPath = Paths.get(storeLocation, jobId);

        failIfJobDoesNotExists(jobId, jobPath);

        RegisterJobInfo job = loadJobInfo(jobId, jobPath);

        saveJobStage(newStageInfo, jobPath, job);
        subscriptionHandler.notifySubscriber(job, newStageInfo);

    }

    @Override
    public void deleteJob(String jobId) {

    }

    @Override
    public List<RegisterJobInfo> jobs() {
        Path jobPath = Paths.get(storeLocation);
        return listJobInfoFiles(jobPath)
                .map(folder -> toJobInfoFiles(folder))
                .filter(files -> files.length > 0)
                .map(files -> files[0])
                .map(jobInfoFile -> loadJob(jobInfoFile))
                .collect(Collectors.toList());

    }

    @Override
    public RegisterJobInfo job(String jobId) {
        Path jobPath = Paths.get(storeLocation, jobId);
        failIfJobDoesNotExists(jobId, jobPath);

        return loadJob(new File(jobPath.toFile(), JOB_INFO_FILE));
    }

    @Override
    public JobStages stages(String jobId) {

        Path jobPath = Paths.get(storeLocation, jobId);
        failIfJobDoesNotExists(jobId, jobPath);

        File stageInfoFile = new File(jobPath.toFile(), STAGE_INFO_FILE);
        if (stageInfoFile.exists()) {
            return loadJobStages(stageInfoFile);
        }
        return new JobStages();
    }


    private RegisterJobInfo loadJobInfo(String jobId, Path jobPath) {
        cache.computeIfAbsent(jobId, key -> loadJobInfo(jobPath));
        return cache.get(jobId);
    }

    private void saveJobStage(JobStageInfo newStageInfo, Path jobPath, RegisterJobInfo job) {
        synchronized (job) {
            File stageInfoFile = new File(jobPath.toFile(), STAGE_INFO_FILE);
            JobStages stages = getOrNewJobStage(stageInfoFile);
            addNewStage(newStageInfo, stages);
            logger.info("Job stage info will be written to {}", stageInfoFile.getAbsolutePath());
            write(stageInfoFile.toPath(), () -> JsonConverter.toJson(stages).getBytes());
        }
    }

    private RegisterJobInfo loadJobInfo(Path jobPath) {
        File jobInfoFile = new File(jobPath.toFile(), JOB_INFO_FILE);
        logger.info("Job info will be read from {}", jobInfoFile.getAbsolutePath());
        return loadJob(jobInfoFile);
    }

    private void addNewStage(JobStageInfo newStageInfo, JobStages stages) {
        newStageInfo.setTimestamp(LocalDateTime.now());
        stages.getStages().add(newStageInfo);
    }

    private JobStages getOrNewJobStage(File stageInfoFile) {
        if (stageInfoFile.exists()) {
            return loadJobStages(stageInfoFile);
        }
        return new JobStages();
    }

    private JobStages loadJobStages(File stageInfoFile) {
        byte[] data = read(stageInfoFile.toPath());
        return fromJson(data, JobStages.class);
    }

    private byte[] read(Path location) {
        try {
            return Files.readAllBytes(location);
        } catch (Exception e) {
            Panic.raise(e);
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
            throw new IllegalArgumentException(String.format("Job %s is not registered", jobId));
        }
    }

    private RegisterJobInfo loadJob(File jobInfoFile) {
        byte[] data = read(jobInfoFile.toPath());
        return fromJson(data, RegisterJobInfo.class);
    }


    private Stream<Path> listJobInfoFiles(Path jobPath) {
        try {
            return Files.list(jobPath);
        } catch (Exception e) {
            Panic.raise(e);
        }
        return null;
    }

    private File[] toJobInfoFiles(Path folder) {
        return folder.toFile().listFiles(file -> jobInfoPredicate(file));
    }

    private boolean jobInfoPredicate(File file) {
        return file.isFile() && file.getName().equalsIgnoreCase(JOB_INFO_FILE);
    }
}
