package site.raylambytes.aijobmatcher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String chromeDriverPath;
    private boolean offlineMode;
    private List<String> aiModels;
    private String aiEndpoint;
    private String apiKey;
    private List<JobConfig> jobConfigs;

    public String getChromeDriverPath() {
        return chromeDriverPath;
    }

    public void setChromeDriverPath(String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    public List<String> getAiModels() {
        return aiModels;
    }

    public void setAiModels(List<String> aiModels) {
        this.aiModels = aiModels;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAiEndpoint() {
        return aiEndpoint;
    }

    public void setAiEndpoint(String aiEndpoint) {
        this.aiEndpoint = aiEndpoint;
    }

    public List<JobConfig> getJobConfigs() {
        return jobConfigs;
    }

    public void setJobConfigs(List<JobConfig> jobConfigs) {
        this.jobConfigs = jobConfigs;
    }
}
