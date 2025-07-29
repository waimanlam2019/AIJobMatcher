package site.raylambytes.aijobmatcher;


import org.junit.jupiter.api.Test;
import site.raylambytes.aijobmatcher.util.PromptBuilder;

import static org.junit.jupiter.api.Assertions.*;

class PromptBuilderTest {

    @Test
    void buildPrompt_containsCandidateAndJobDescription() {
        // Arrange
        String roleplay = "You are an experienced HR AI.";
        String profile = "Ray is a Java developer with 10 years of experience.";
        String jobDesc = "We are looking for a Java backend engineer with 8+ years experience.";
        String task = "Compare the candidate to the job requirements.";

        PromptBuilder builder = new PromptBuilder(roleplay, profile, jobDesc, task);

        // Act
        String result = builder.buildPrompt();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Ray is a Java developer"));
        assertTrue(result.contains("We are looking for a Java backend engineer"));
        assertTrue(result.contains("experienced HR AI"));
        assertTrue(result.contains("Compare the candidate"));
    }

    @Test
    void buildPrompt_escapesSpecialCharactersCorrectly() {
        // Arrange
        String roleplay = "AI role: \"Judge\"";
        String profile = "Ray said: \"I love Java\"\nNew line.";
        String jobDesc = "Job includes: C++, \"Python\", and Spring\nAlso: AWS";
        String task = "Evaluate fit and return \\n-safe response.";

        PromptBuilder builder = new PromptBuilder(roleplay, profile, jobDesc, task);

        // Act
        String result = builder.buildPrompt();

        // Assert
        assertFalse(result.contains("\n"), "Prompt should not contain raw newlines");
        assertTrue(result.contains("\\n"), "Escaped newline expected");
        assertTrue(result.contains("\\\""), "Escaped quotes expected");
    }

    @Test
    void buildPrompt_startsWithExpectedIntro() {
        // Arrange
        String roleplay = "You are a brutally honest AI HR assistant.";
        String profile = "P";
        String jobDesc = "J";
        String task = "T";

        PromptBuilder builder = new PromptBuilder(roleplay, profile, jobDesc, task);

        // Act
        String result = builder.buildPrompt();

        // Assert
        assertTrue(result.startsWith("You are a brutally honest"), "Prompt should start with AI roleplay");
    }
}
