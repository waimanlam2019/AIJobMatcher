package site.raylambytes.aijobmatcher;

import java.util.List;

public class JobConfig {

    private String initUrl;
    private int maxPages;
    private boolean rematch;
    private String aiRoleplay;
    private String aiTask;
    private String candidateProfile;

    public String getInitUrl() {
        return initUrl;
    }

    public void setInitUrl(String initUrl) {
        this.initUrl = initUrl;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    public boolean isRematch() {
        return rematch;
    }

    public void setRematch(boolean rematch) {
        this.rematch = rematch;
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
}
