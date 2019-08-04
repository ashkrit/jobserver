package jobserver.client.impl;

import jobserver.client.JobServerClient;
import jobserver.client.json.JsonConverter;
import jobserver.client.lang.Panic;
import jobserver.client.protocol.JobStageInfo;
import jobserver.client.protocol.JobStagesResponse;
import jobserver.client.protocol.JobsResponse;
import jobserver.client.protocol.RegisterJobInfo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/*
    This client is written in lower level http connection API to reduce third party dependency , so that
    it can be used from micro service/standalone java client
 */
public class SimpleHTTPJobServerClient implements JobServerClient {

    private static final String POST = "POST";
    private static final String USER_AGENT = "Java API Client";
    private static final String APPLICATION_JSON = "application/json";
    public static final String GET = "GET";
    public static final int HTTP_OK = 200;

    private final String server;

    public SimpleHTTPJobServerClient(String server) {
        this.server = String.format("%s/jobserver", server);
    }

    public static SimpleHTTPJobServerClient create(String server) {
        return new SimpleHTTPJobServerClient(server);
    }

    @Override
    public void registerJob(RegisterJobInfo jobInfo) throws Exception {

        String registerJob = String.format("%s/jobs/registerJob", server);
        HttpURLConnection httpConnection = createPostHttpConnection(registerJob);

        write(jobInfo, httpConnection);
        readResponseData(httpConnection);

        httpConnection.disconnect();
    }

    @Override
    public void stageUpdate(JobStageInfo stageInfo) throws Exception {
        String stageUpdate = String.format("%s/jobs/stage", server);
        HttpURLConnection httpConnection = createPostHttpConnection(stageUpdate);

        write(stageInfo, httpConnection);
        readResponseData(httpConnection);

        httpConnection.disconnect();
    }

    @Override
    public JobsResponse jobs() throws Exception {
        String jobs = String.format("%s/jobs", server);
        HttpURLConnection httpConnection = createGetHttpConnection(jobs);
        int responseCode = httpConnection.getResponseCode();
        if (responseCode != HTTP_OK) {
            Panic.raise(new RuntimeException(String.format("Job server returned %s", responseCode)));
        }
        String data = readResponseData(httpConnection);
        return JsonConverter.fromJson(data.getBytes(), JobsResponse.class);
    }

    @Override
    public JobStagesResponse jobStages(String id) throws Exception {
        String jobs = String.format("%s/jobs/stage/%s", server, id);
        HttpURLConnection httpConnection = createGetHttpConnection(jobs);
        int responseCode = httpConnection.getResponseCode();
        if (responseCode != HTTP_OK) {
            Panic.raise(new RuntimeException(String.format("Job server returned %s", responseCode)));
        }
        String data = readResponseData(httpConnection);
        return JsonConverter.fromJson(data.getBytes(), JobStagesResponse.class);
    }

    private <T> void write(T message, HttpURLConnection httpConnection) throws IOException {
        try (DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream())) {
            wr.write(JsonConverter.toJson(message).getBytes());
        }
    }

    private HttpURLConnection createPostHttpConnection(String registerJob) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(registerJob).openConnection();
        httpConnection.setRequestMethod(POST);
        httpConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpConnection.setRequestProperty("Content-Type", APPLICATION_JSON);
        httpConnection.setDoOutput(true);
        return httpConnection;
    }

    private HttpURLConnection createGetHttpConnection(String registerJob) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(registerJob).openConnection();
        httpConnection.setRequestMethod(GET);
        httpConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpConnection.setRequestProperty("Content-Type", APPLICATION_JSON);
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
