RESTful interface for managing general purpose Jobs

## Getting Started

### Build module

mvn clean install

### Run server from IDE
JobServerApplication

### Run from command line
java -jar server/target/server-1.0-SNAPSHOT.jar

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
JobServerClient client = JobServerClient.create("http://localhost:8080/");

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