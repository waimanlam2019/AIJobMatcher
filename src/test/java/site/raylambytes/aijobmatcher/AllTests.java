package site.raylambytes.aijobmatcher;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ResponseAnalyzerTest.class,
        PromptBuilderTest.class
})
public class AllTests {
}