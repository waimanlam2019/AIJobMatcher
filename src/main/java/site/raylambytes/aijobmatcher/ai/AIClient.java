package site.raylambytes.aijobmatcher.ai;

public interface AIClient {
    String query(String prompt);
    String getAiModel();
}