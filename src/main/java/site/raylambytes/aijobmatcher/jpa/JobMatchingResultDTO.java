package site.raylambytes.aijobmatcher.jpa;

import java.time.LocalDateTime;

public class JobMatchingResultDTO {
    private String title;
    private String company;
    private String location;
    private String jobType;
    private String description;
    private String url;
    private String aiModel;
    private String verdict;
    private boolean shortlistFlag;
    private java.time.LocalDateTime createdAt;
    public JobMatchingResultDTO(){

    }
    public JobMatchingResultDTO(String title, String company, String location, String jobType, String description, String url, String aiModel, String verdict, boolean shortlistFlag, LocalDateTime createdAt) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.jobType = jobType;
        this.description = description;
        this.url = url;
        this.aiModel = aiModel;
        this.verdict = verdict;
        this.shortlistFlag = shortlistFlag;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public boolean isShortlistFlag() {
        return shortlistFlag;
    }

    public void setShortlistFlag(boolean shortlistFlag) {
        this.shortlistFlag = shortlistFlag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
