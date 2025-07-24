package site.raylambytes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PromptBuilderTest {

    @Test
    void buildPrompt_containsCandidateAndJobDescription() {
        // Arrange
        String profile = "Ray is a Java developer with 10 years of experience.";
        String jobDesc = "We are looking for a Java backend engineer with 8+ years experience.";
        PromptBuilder builder = new PromptBuilder(profile, jobDesc);

        // Act
        String result = builder.buildPrompt();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Ray is a Java developer"));
        assertTrue(result.contains("We are looking for a Java backend engineer"));
    }


    @Test
    void buildPrompt_escapesSpecialCharactersCorrectly() {
        String profile = "Ray said: \"I love Java\"\nNew line.";
        String jobDesc = "Job includes: C++, \"Python\", and Spring\nAlso: AWS";
        PromptBuilder builder = new PromptBuilder(profile, jobDesc);

        String result = builder.buildPrompt();

        assertFalse(result.contains("\n"));
        assertTrue(result.contains("\\n"));  // newlines escaped
        assertTrue(result.contains("\\\"")); // quotes escaped
    }

    @Test
    void buildPrompt_startsWithExpectedIntro() {
        PromptBuilder builder = new PromptBuilder("P", "J");

        String result = builder.buildPrompt();

        assertTrue(result.startsWith("You are a brutally honest"));
    }
}
