package site.raylambytes.aijobmatcher.ai;
import org.json.JSONObject;

import java.util.List;
public interface AIClient {
    String query(String prompt, String aiModel);
    List<String> getAiModels();
    Long getTokenUsage();
}