package site.raylambytes;

public class PromptBuilder {
    private final String candidateProfile;
    private final String jobDescriptionText;

    public PromptBuilder(String candidateProfile, String jobDescriptionText) {
        this.candidateProfile = candidateProfile;
        this.jobDescriptionText = jobDescriptionText;
    }

    public String buildPrompt() {
        String prompt = "You are a brutally honest, no-nonsense senior technical recruiter. "
                + "Your job is to critically evaluate candidates with *strict adherence* to the job requirements, "
                + "with *zero tolerance* for missing skills or vague experience.\n\n"
                + "Here is the job description:\n"
                + jobDescriptionText + "\n\n"
                + "Here is the candidate profile:\n"
                + candidateProfile + "\n\n"
                + "Evaluate the candidate's fitness *strictly*. Only award points for exact matches with required skills, "
                + "technologies, experience, or domain knowledge.\n\n"
                + "Do not give any benefit of the doubt. Do not assume the candidate can 'learn on the job' — if something is missing, treat it as a major gap.\n\n"
                + "Your output must include the following sections:\n"
                + "1. Suitability Score: Give a number from 0 to 10 (no inflation).\n"
                + "2. Matching Strengths: List the exact skills and experiences that match the job requirements.\n"
                + "3. Gaps and Mismatches: List all missing or mismatched skills, outdated tools, irrelevant experience, or unclear info.\n"
                + "4. Verdict: A blunt one-sentence assessment of overall fit.\n"
                + "5. **Shortlist Flag: YES or NO** — in uppercase, on a separate line by itself, and nothing else on the line.\n\n"
                + "Example:\n"
                + "Shortlist Flag:\n"
                + "NO\n\n"
                + "Your answer starts now:";
        return prompt
                .replace("\\", "\\\\")   // escape backslash first
                .replace("\"", "\\\"")   // escape double quotes
                .replace("\n", "\\n");
    }

}
