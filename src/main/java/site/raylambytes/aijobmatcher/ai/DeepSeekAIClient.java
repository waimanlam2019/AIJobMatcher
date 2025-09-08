package site.raylambytes.aijobmatcher.ai;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import site.raylambytes.aijobmatcher.AppConfig;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Profile("deepseek")
@Primary
@Service
public class DeepSeekAIClient implements AIClient{
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekAIClient.class);

    private final List<String> aiModels;
    private final HttpClient client;
    private final String endpoint;
    private final String apiKey;
    private Long tokenUsage = 0L;

    public DeepSeekAIClient(AppConfig appConfig) {
        this.aiModels = appConfig.getAiModels();
        this.client = HttpClient.newHttpClient();
        this.endpoint = appConfig.getAiEndpoint();
        this.apiKey = appConfig.getApiKey();
    }

    @Override
    public String query(String prompt, String aiModel) {
        // Build messages array
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."));
        messages.put(new JSONObject().put("role", "user").put("content", prompt));

        // Build payload
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("model", aiModel);
        jsonPayload.put("messages", messages);
        jsonPayload.put("stream", false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString()))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("DeepSeek response: {}", response.body());
        } catch (IOException | InterruptedException e) {
            logger.error("❌ Error querying DeepSeek API: ", e);
            throw new RuntimeException(e);
        }

        // Parse response
        JSONObject obj = new JSONObject(response.body());
        tokenUsage += obj.getJSONObject("usage").getInt("total_tokens");

        JSONArray choices = obj.getJSONArray("choices");
        String answer = choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        return answer;
    }

    @Override
    public List<String> getAiModels() {
        return aiModels;
    }


    @Override
    public Long getTokenUsage() {
        return tokenUsage;
    }
}
