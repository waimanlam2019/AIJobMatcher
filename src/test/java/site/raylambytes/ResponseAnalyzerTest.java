package site.raylambytes;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseAnalyzerTest {

    @Test
    public void testMatchSameLine() {
        String input = "Shortlist Flag: YES";
        assertTrue(ResponseAnalyzer.isJobGoodToApply(input));
    }

    @Test
    public void testMatchWithLineBreak() {
        String input = "Shortlist Flag:\nYES";
        assertTrue(ResponseAnalyzer.isJobGoodToApply(input));
    }

    @Test
    public void testMatchWithExtraWhitespace() {
        String input = "Shortlist Flag:   \n   YES";
        assertTrue(ResponseAnalyzer.isJobGoodToApply(input));
    }

    @Test
    public void testDoesNotMatchNoFlag() {
        String input = "Shortlist Flag: NO";
        assertFalse(ResponseAnalyzer.isJobGoodToApply(input));
    }

    @Test
    public void testDoesNotMatchRandomText() {
        String input = "This is not the flag you're looking for.";
        assertFalse(ResponseAnalyzer.isJobGoodToApply(input));
    }
}
