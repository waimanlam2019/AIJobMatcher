package site.raylambytes.aijobmatcher.ai;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.raylambytes.aijobmatcher.AppConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OllamaAIClient implements AIClient {
    private static final Logger logger = LoggerFactory.getLogger(OllamaAIClient.class);

    private final String aiModel;
    private final HttpClient client;
    private final String endpoint;

    public OllamaAIClient(AppConfig appConfig) {
        this.aiModel = appConfig.getAiModel();
        this.client = HttpClient.newHttpClient();
        this.endpoint = appConfig.getAiEndpoint();
    }

    @Override
    public String query(String prompt) {
        String jsonPayload = "{\"model\":\"" + aiModel + "\", \"prompt\":\"" + prompt + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.error("❌ Error querying AI model: ", e);
            throw new RuntimeException(e);
        }

        JSONObject obj = new JSONObject(response.body());
        JSONArray choices = obj.getJSONArray("choices");
        return choices.getJSONObject(0).getString("text");
    }

    @Override
    public String getAiModel() {
        return aiModel;
    }

    @Override
    public String toString() {
        return "OllamaAIClient{" +
                "aiModel='" + aiModel + '\'' +
                ", client=" + client +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
