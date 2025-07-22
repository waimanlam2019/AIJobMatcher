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

        WebDriver listDriver = new ChromeDriver(options);
        WebDriver detailDriver = new ChromeDriver(options);

        try {

            listDriver .get(INIT_URL);

            // Wait for jobs to load (basic sleep; for production use WebDriverWait)
            Thread.sleep(5000);

            // Find all job cards by their attribute
            List<WebElement> jobList = listDriver .findElements(By.cssSelector("article[data-card-type=JobCard]"));
            logger.info("Found " + jobList.size() + " job(s) on jobsdb link.");
            if (jobList.isEmpty()) {
                logger.info("❌ No job found. Exiting.");
                return;
            }

            int jobCount = 0;
            for (WebElement job : jobList) {
                logger.info("Checking job...");
                if (jobCount>MAX_JOBS){
                    return;
                }
                String title = job.findElement(By.cssSelector("a[data-automation=jobTitle]")).getText();
                String company = job.findElement(By.cssSelector("a[data-automation=jobCompany]")).getText();
                String location = job.findElement(By.cssSelector("span[data-automation=jobLocation]")).getText();
                String jobUrl = job.findElement(By.cssSelector("a[data-automation=jobTitle]")).getAttribute("href");

                logger.info("\uD83D\uDD39 Title: {}", title);//Check
                logger.info("\uD83C\uDFE2 Company: {}", company);//Check
                logger.info("\uD83D\uDCCD Location: {}", location);
                logger.info("\uD83D\uDD17 URL: {}", jobUrl);
                logger.info("--------------------------------------------------");

                // After navigating to the job URL, e.g.:
                detailDriver.get(jobUrl);

                // Wait for page load, simple Thread.sleep or WebDriverWait recommended
                Thread.sleep(4000);

                // Locate the job description container by data-automation attribute
                WebElement jobDescDiv = detailDriver.findElement(By.cssSelector("div[data-automation='jobAdDetails']"));

                // Get the full HTML inside the job description container
                String jobDescriptionHtml = jobDescDiv.getAttribute("innerHTML");

                // Or get only the visible text (stripped of tags)
                String jobDescriptionText = jobDescDiv.getText();

                String candidateProfile = "Wai Man Lam (Ray) is a Hong Kong permanent resident and experienced software engineer with over 10 years in software development and system analysis. He holds a Computer Science degree from Hong Kong Baptist University, where he worked with Python on his final year project.\n" +
                        "\n" +
                        "Ray’s recent experience includes senior roles at TransUnion Hong Kong and Currencyfair Asia Ltd, where he managed Agile SDLC, developed backend REST APIs with Java Spring MVC and Spring Boot, integrated external APIs, and enhanced web portals with JavaScript, React, and microservices architecture. He has strong expertise in CI/CD pipelines using Jenkins, Git, Maven, and container tools like Docker and Podman.\n" +
                        "\n" +
                        "He is skilled in Java, Spring Framework, reactive programming, TDD/BDD practices, and has experience working with offshore teams. Ray also has a proven track record integrating digital onboarding and security solutions and managing cross-functional projects.\n" +
                        "\n" +
                        "Ray is fluent in English and Cantonese with conversational Mandarin. Currently available for immediate hire, he continuously studies modern technologies like React, microservices, and AI tools.";

                String prompt = "You are a brutally honest, no-nonsense senior technical recruiter. "
                        + "Your job is to critically evaluate candidates with *strict adherence* to the job requirements, "
                        + "with *zero tolerance* for missing skills or vague experience.\n\n"
                        + "Here is the job description:\n"
                        + jobDescriptionText + "\n\n"
                        + "Here is the candidate profile:\n"
                        + candidateProfile + "\n\n"
                        + "Evaluate the candidate's fitness *strictly*. Only award points for exact matches with required skills, "
                        + "technologies, experience, or domain knowledge.\n\n"
                        + "Do not give any benefit of the doubt. Do not assume the candidate can 'learn on the job' — if something is missing, treat it as a major gap.\n\n"
                        + "Your output must include the following sections:\n"
                        + "1. Suitability Score: Give a number from 0 to 10 (no inflation).\n"
                        + "2. Matching Strengths: List the exact skills and experiences that match the job requirements.\n"
                        + "3. Gaps and Mismatches: List all missing or mismatched skills, outdated tools, irrelevant experience, or unclear info.\n"
                        + "4. Verdict: A blunt one-sentence assessment of overall fit.\n"
                        + "5. **Shortlist Flag: YES or NO** — in uppercase, on a separate line by itself, and nothing else on the line.\n\n"
                        + "Example:\n"
                        + "Shortlist Flag:\n"
                        + "NO\n\n"
                        + "Your answer starts now:";

                String escapedPrompt = prompt
                        .replace("\\", "\\\\")   // escape backslash first
                        .replace("\"", "\\\"")   // escape double quotes
                        .replace("\n", "\\n");   // escape newlines

                String jsonPayload = "{\"model\":\"mistral\", \"prompt\":\"" + escapedPrompt + "\"}";

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/v1/completions"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                JSONObject obj = new JSONObject(response.body());
                JSONArray choices = obj.getJSONArray("choices");
                String suggestion = choices.getJSONObject(0).getString("text");

                // Print it nicely
                logger.info("Ollama Suggestion:\n{}", suggestion.trim());

               if (suggestion.matches("(?s).*Shortlist Flag:\\s*YES.*")) {
                    String subject = "💼 New Job Match Found!" + title + " at " + company;
                    String body = "Title: " + title + "<br/>" + "Company: " + company + "<br/>" + jobUrl + "<br/><br/>";
                    body += "You might be a good fit for this job:<br/>" + "<div style=\"border: 2px solid #333333; padding: 15px; border-radius: 6px; margin-bottom: 20px;\">"+jobDescriptionHtml+"</div>" + "<br/>AI says<br/>" + "<div style=\"background-color: #f0f4f8; border-left: 4px solid #3b82f6; padding: 15px; margin-top: 20px; font-family: monospace; white-space: pre-wrap;line-height: 1.5;\">"+"<pre>" +suggestion.trim() +"</pre></div>";

                    EmailNotifier.sendEmail(subject, body);
               }

                jobCount++;
            }
        } catch (Exception e) {
            logger.error("An expected error occurred during scraping: ", e);
        } finally {
            listDriver.quit();
            detailDriver.quit();
        }
    }
}
