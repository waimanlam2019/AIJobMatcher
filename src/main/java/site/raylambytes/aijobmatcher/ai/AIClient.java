package site.raylambytes.aijobmatcher.ai;
import java.util.List;
public interface AIClient {
    String query(String prompt, String aiModel);
    Long estimateTokenUsage(String prompt);

    List<String> getAiModels();
}