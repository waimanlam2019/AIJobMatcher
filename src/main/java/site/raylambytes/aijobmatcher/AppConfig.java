package site.raylambytes.aijobmatcher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String chromeDriverPath;
    private String initUrl;
    private String maxJobs;
    private String aiModel;
    private String aiEndpoint;
    private String aiRoleplay;
    private String aiTask;
    private String candidateProfile;
    private String emailFrom;
    private String emailTo;
    private String emailPassword;

    public String getChromeDriverPath() {
        return chromeDriverPath;
    }

    public void setChromeDriverPath(String chromeDriverPath) {
        this.chromeDriverPath = chromeDriverPath;
    }

    public String getInitUrl() {
        return initUrl;
    }

    public void setInitUrl(String initUrl) {
        this.initUrl = initUrl;
    }

    public String getMaxJobs() {
        return maxJobs;
    }

    public void setMaxJobs(String maxJobs) {
        this.maxJobs = maxJobs;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getAiEndpoint() {
        return aiEndpoint;
    }

    public void setAiEndpoint(String aiEndpoint) {
        this.aiEndpoint = aiEndpoint;
    }

    public String getAiRoleplay() {
        return aiRoleplay;
    }

    public void setAiRoleplay(String aiRoleplay) {
        this.aiRoleplay = aiRoleplay;
    }

    public String getAiTask() {
        return aiTask;
    }

    public void setAiTask(String aiTask) {
        this.aiTask = aiTask;
    }

    public String getCandidateProfile() {
        return candidateProfile;
    }

    public void setCandidateProfile(String candidateProfile) {
        this.candidateProfile = candidateProfile;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "chromeDriverPath='" + chromeDriverPath + '\'' +
                ", initUrl='" + initUrl + '\'' +
                ", maxJobs='" + maxJobs + '\'' +
                ", aiModel='" + aiModel + '\'' +
                ", aiEndpoint='" + aiEndpoint + '\'' +
                ", aiRoleplay='" + aiRoleplay + '\'' +
                ", aiTask='" + aiTask + '\'' +
                ", candidateProfile='" + candidateProfile + '\'' +
                ", emailFrom='" + emailFrom + '\'' +
                ", emailTo='" + emailTo + '\'' +
                ", emailPassword='" + emailPassword + '\'' +
                '}';
    }
}
