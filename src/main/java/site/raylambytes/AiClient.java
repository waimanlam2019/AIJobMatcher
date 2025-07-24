package site.raylambytes;

public interface AiClient {
    String query(String prompt);
    boolean isJobGoodToApply(String queryResult);
}