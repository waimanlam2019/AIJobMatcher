package site.raylambytes;

public class PromptBuilder {
    private final String aiRoleplay;
    private final String candidateProfile;
    private final String jobDescriptionText;
    private final String aiTask;

    public PromptBuilder(String aiRoleplay, String candidateProfile, String jobDescriptionText, String aiTask) {
        this.aiRoleplay = aiRoleplay;
        this.candidateProfile = candidateProfile;
        this.jobDescriptionText = jobDescriptionText;
        this.aiTask = aiTask;
    }

    public String buildPrompt() {
        String prompt =
                aiRoleplay + "\n\n"
                + "Here is the job description:\n"
                + jobDescriptionText + "\n\n"
                + "Here is the candidate profile:\n"
                + candidateProfile + "\n\n"
                + "Here is your task: \n\n"
                + aiTask;
        return prompt
                .replace("\\", "\\\\")   // escape backslash first
                .replace("\"", "\\\"")   // escape double quotes
                .replace("\n", "\\n");
    }

}
