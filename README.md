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
	"eventName":"European-Cental-Bank-Started"
	"properties": {
        "remarks":"Late by 15 min"
	}
}
```