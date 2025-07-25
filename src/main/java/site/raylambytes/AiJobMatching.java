package site.raylambytes;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AiJobMatching {
    private static final String INIT_URL = "https://hk.jobsdb.com/jobs-in-information-communication-technology?sortmode=ListedDate";
    private static final Integer MAX_JOBS = Integer.valueOf(ConfigLoader.get("max.jobs")); // Limit to 10 jobs
    private static final Logger logger = LoggerFactory.getLogger(AiJobMatching.class);// for demo purposes
    public static void main(String[] args) {

        // Setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        // Headless Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // new headless mode for Chrome > 109
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");

        System.setProperty("webdriver.chrome.driver", ConfigLoader.get("chrome.driver.path"));

        WebDriver listWebDriver = new ChromeDriver(options);
        WebDriver detailWebDriver = new ChromeDriver(options);
        SeleniumJobScraper jobScraper = new SeleniumJobScraper(listWebDriver, detailWebDriver,  INIT_URL);

        try {
            // Find all job cards by their attribute
            List<WebElement> jobCardList = jobScraper.scrapeJobCardListing();

            jobCardList.stream()
                    .limit(MAX_JOBS)
                    .forEach(jobCard -> {
                        JobPosting jobPosting = jobScraper.digestJobCard(jobCard);
                        jobPosting = jobScraper.scrapeJobDetails(jobPosting);

                        String candidateProfile = ConfigLoader.get("candidate.profile");
                        PromptBuilder promptBuilder = new PromptBuilder(candidateProfile, jobPosting.getDescription());
                        String prompt = promptBuilder.buildPrompt();
                        logger.info("Prompt: {}", prompt);
                        String aiModel = ConfigLoader.get("ai.model");
                        logger.info("Using AI model: {}", aiModel);
                        AiClient aiClient = new OllamaAiClient(aiModel, HttpClient.newHttpClient(), "http://localhost:11434/v1/completions");
                        String suggestion = aiClient.query(prompt);

                        // Print it nicely
                        logger.info("Ollama Suggestion:\n{}", suggestion.trim());

                        if (aiClient.isJobGoodToApply(suggestion)) {
                            String subject = "💼 New Job Match Found!" + jobPosting.getTitle() + " at " + jobPosting.getCompany();
                            String body = "Title: " + jobPosting.getTitle() + "<br/>" + "Company: " + jobPosting.getCompany() + "<br/>" + jobPosting.getUrl() + "<br/><br/>You might be a good fit for this job:<br/>" ;
                            String jobDescriptionStyle="<div style=\"border: 2px solid #333333; padding: 15px; border-radius: 6px; margin-bottom: 20px;\">";
                            body += jobDescriptionStyle + jobPosting.getDescriptionHtml();
                            String aiSuggestionStyle="<div style=\"background-color: #f0f4f8; border-left: 4px solid #3b82f6; padding: 15px; margin-top: 20px; font-family: monospace; white-space: pre-wrap;line-height: 1.5;\">";
                            body+="</div><br/>AI says<br/>" + aiSuggestionStyle + "<pre>" +suggestion.trim() +"</pre></div>";

                            EmailNotifier.sendEmail(subject, body);
                        }
                    });
        } catch (Exception e) {
            logger.error("An expected error occurred during scraping: ", e);
        } finally {
            listWebDriver.quit();
            detailWebDriver.quit();
        }
    }
}
