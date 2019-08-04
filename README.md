RESTful interface for managing general purpose Jobs

## Getting Started

### Build module

mvn clean install

### Run server from IDE
JobServerApplication

### Run from command line
java -jar server/target/server-1.0-SNAPSHOT.jar
java -Djobserver.store.location=/tmp/jobservertest -jar server/target/server-1.0-SNAPSHOT.jar

### Rest API examples

Register JOB - POST - http://localhost:8080/jobserver/jobs/registerJob
```
{
	"jobId":"20190804_020000",
	"jobName":"FX Rates",
	"category":"Daily",
	"subscriptions": [
		{"type":"REST" , "properties":{
			"endpoint":"http://localhost:8081/jobserver/callback/stage"
		}}
		]
}
```

Send stage update - POST - http://localhost:8080/jobserver/jobs/stage
```
{
	"jobId":"20190804_020000",
	"stageName":"LoadFXRate",
	"eventName":"European-Cental-Bank-Started"
}
```

With some extra context in job

```
{
	"jobId":"20190804_020000",
	"stageName":"LoadFXRate",
	"eventName":"European-Central-Bank-Started"
	"properties": {
        "remarks":"Late by 15 min"
	}
}
```

### Java Client

Code snippet from JobServerClientApplication.java

```
JobServerClient client = JobServerClient.defaultClient("http://localhost:8080/");

        String jobId = "20190804_FXUPDATE_RUN2";
        String value = "http://localhost:8080/jobserver/callback/stage";

        RegisterJobInfo job = createNewJob(jobId, value);
        client.registerJob(job);

        JobStageInfo stage = new JobStageInfo();
        stage.setJobId(jobId);
        stage.setStageName("LoadFXRate");
        stage.setEventName("European-Central-Bank-Started");

        client.stageUpdate(stage);

```

### Other APIS

Get all Jobs
GET - http://localhost:8080/jobserver/jobs

```
{"jobs":[{"jobId":"20190804_FXUPDATE_RUN2","jobName":"This is Random job","category":"test","subscriptions":[{"type":"REST","properties":{"endpoint":"http://localhost:8080/jobserver/callback/stage"}}],"timestamp":"2019-08-04T03:25:32.5705388"}]}
```

Get Job Stages details
GET - http://localhost:8080/jobserver/jobs/stage/20190804_FXUPDATE_RUN2

```
{"job":{"jobId":"20190804_FXUPDATE_RUN2","jobName":"This is Random job","category":"test","subscriptions":[{"type":"REST","properties":{"endpoint":"http://localhost:8080/jobserver/callback/stage"}}],"timestamp":"2019-08-04T03:25:32.5705388"},"stages":[{"jobId":"20190804_FXUPDATE_RUN2","stageName":"LoadFXRate","eventName":"European-Central-Bank-Started","timestamp":"2019-08-04T03:25:32.609435"},{"jobId":"20190804_FXUPDATE_RUN2","stageName":"LoadFXRate","eventName":"European-Central-Bank-Started","timestamp":"2019-08-04T03:44:18.4558119"}]}
```

Java client
```

public interface JobServerClient {

    void registerJob(RegisterJobInfo jobInfo) throws Exception;

    void stageUpdate(JobStageInfo stageInfo) throws Exception;

    JobsResponse jobs() throws Exception;

    JobStagesResponse jobStages(String id) throws Exception;

}
```