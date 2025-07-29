package site.raylambytes.aijobmatcher.util;

public class ResponseAnalyzer {

    public static boolean isJobGoodToApply(String queryResult) {
        String cleaned = queryResult.replaceAll("[*_`~]+", "").trim();
        return cleaned.matches("(?s).*Shortlist Flag:\\s*YES.*");
    }
}
