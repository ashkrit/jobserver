package jobserver.client;

import jobserver.client.json.JsonConverter;
import jobserver.client.lang.Panic;
import jobserver.client.protocol.JobStageInfo;
import jobserver.client.protocol.RegisterJobInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/*
    This client is written in lower level http connection API to reduce third party dependency.
    THis client can be used from microservice or Spark job
 */
public class JobServerClient {

    private static final String POST = "POST";
    private static final String USER_AGENT = "Java API Client";
    private static final String APPLICATION_JSON = "application/json";

    private final String server;

    public JobServerClient(String server) {
        this.server = String.format("%s/jobserver", server);
    }

    public static JobServerClient create(String server) {
        return new JobServerClient(server);
    }

    public void registerJob(RegisterJobInfo jobInfo) throws Exception {

        String registerJob = String.format("%s/jobs/registerJob", server);
        HttpURLConnection httpConnection = createHttpConnection(registerJob);

        write(jobInfo, httpConnection);
        readResponseData(httpConnection);

        httpConnection.disconnect();
    }

    public void stageUpdate(JobStageInfo stageInfo) throws Exception {
        String registerJob = String.format("%s/jobs/stage", server);
        HttpURLConnection httpConnection = createHttpConnection(registerJob);

        write(stageInfo, httpConnection);
        readResponseData(httpConnection);

        httpConnection.disconnect();
    }

    private <T> void write(T message, HttpURLConnection httpConnection) throws IOException {
        try (DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream())) {
            wr.write(JsonConverter.toJson(message).getBytes());
        }
    }

    private HttpURLConnection createHttpConnection(String registerJob) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(registerJob).openConnection();
        httpConnection.setRequestMethod(POST);
        httpConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpConnection.setRequestProperty("Content-Type", APPLICATION_JSON);
        httpConnection.setDoOutput(true);
        return httpConnection;
    }

    private String readResponseData(HttpURLConnection httpConnection) throws IOException {
        try {
            return readAsText(httpConnection.getInputStream());
        } catch (Exception e) {
            Panic.raise(e, readAsText(httpConnection.getErrorStream()));
        }
        return null;
    }

    private String readAsText(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        }
        return content.toString();
    }

}
