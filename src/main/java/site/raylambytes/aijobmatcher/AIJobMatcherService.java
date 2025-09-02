package site.raylambytes.aijobmatcher;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.raylambytes.aijobmatcher.ai.AIClient;
import site.raylambytes.aijobmatcher.jpa.*;
import site.raylambytes.aijobmatcher.scraper.SeleniumJobScraper;
import site.raylambytes.aijobmatcher.util.PromptBuilder;
import site.raylambytes.aijobmatcher.util.ResponseAnalyzer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class AIJobMatcherService {
    private static final Logger logger = LoggerFactory.getLogger(AIJobMatcherService.class);// for demo purposes

    private Future<?> runningTask;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    private static final List<String> bannedJobs = List.of(
            // English
            "nurse", "therapist", "social worker", "counselor", "doctor",
            "clinical", "registered nurse", "engineer (licensed)", "accounting", "accountant", "accounts",
            "certified accountant", "child care", "teacher", "lecturer", "psychologist", "child", "children", "intern","tutor",

            // Chinese //TODO this config is not in .properties because .properties is not UTF-8 encoded and does not support Chinese characters
            "護士", "註冊護士", "社工", "社會工作者", "治療師", "心理治療師",
            "醫生", "臨床", "牙醫", "工程師牌", "註冊工程師", "會計",
            "會計師", "註冊會計師", "教師", "教學", "老師", "心理學家", "幼兒照顧",
            "保母", "導師", "補習", "司機", "鏟車", "治療", "實習", "实习生", "護 士", "舞台"
            );
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AIClient aiClient;

    @Autowired
    private SeleniumJobScraper jobScraper;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private MatchingResultRepository matchingResultRepository;

    public synchronized void startJobMatching() {
        if (runningTask != null && !runningTask.isDone()) {
            throw new IllegalStateException("Job matching is already running");
        }

        // Logic for matching jobs with AI
        runningTask = executor.submit(() -> {
            try {
                scrapeJobPortalsAndDoAiMatching();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        });
    }

    public synchronized void stopJobMatching() {
        if (runningTask != null && !runningTask.isDone()) {
            runningTask.cancel(true); // sends interrupt
        }
    }

    private void queryOllamaAIs(JobPosting jobPosting, JobConfig jobConfig) {
        for ( String aiModel: aiClient.getAiModels() ) {
            if (Thread.currentThread().isInterrupted()) {
                logger.info("AI loop interrupted, stopping matching...");
                return;
            }

            logger.info("Working with ai model: {}", aiModel);
            Optional<MatchingResult> matchingResultInDb = matchingResultRepository.findByJobPostingAndAiModel(jobPosting, aiModel);
            if (matchingResultInDb.isEmpty() || jobConfig.isRematch()) {
                String aiRoleplay = jobConfig.getAiRoleplay();
                String candidateProfile = jobConfig.getCandidateProfile();
                String aiTask = jobConfig.getAiTask();
                PromptBuilder promptBuilder = new PromptBuilder(aiRoleplay, candidateProfile, jobPosting.getDescription(), aiTask);
                String prompt = promptBuilder.buildPrompt();

                logger.info("Prompt: {}", prompt);
                String suggestion = aiClient.query(prompt, aiModel);

                // Print it nicely
                logger.info("Ollama Suggestion:\n{}", suggestion.trim());

                logger.info("Token usage: {}", aiClient.getTokenUsage());


                boolean shortlistFlag = ResponseAnalyzer.isJobGoodToApply(suggestion);
                MatchingResult matchingResult = new MatchingResult();
                matchingResult.setJobPosting(jobPosting);
                matchingResult.setJobId(jobPosting.getJobId());
                matchingResult.setAiModel(aiModel);
                matchingResult.setVerdict(suggestion);
                matchingResult.setShortlistFlag(shortlistFlag);
                matchingResultInDb.ifPresentOrElse(existingResult -> {
                    existingResult.setVerdict(suggestion);
                    existingResult.setShortlistFlag(shortlistFlag);
                    matchingResultRepository.save(existingResult);
                    logger.info("Updated existing matching result for job: {}", existingResult.getJobPosting().getJobId());
                }, () -> {
                    matchingResultRepository.save(matchingResult);
                    logger.info("Saving new matching result for job: {}", matchingResult.getJobPosting().getJobId());
                });
            } else {
                logger.info("Skip matching because matching result already exists for job: {}", jobPosting.getJobId());
            }

        }
    }

    private static boolean isBannedJob(JobPosting jobPosting) {
        String title = jobPosting.getTitle().toLowerCase();
        boolean isBanned = bannedJobs.stream()
                .anyMatch(keyword -> title.contains(keyword.toLowerCase())); // to handle both English & Chinese

        if (isBanned) {
            logger.info("Filtered out unsuitable job: {}", title);
            return true;
        }
        return false;
    }

    public List<JobMatchingResultDTO> getAllJobMatchingResults() {
        return jobPostingRepository.findAllWithMatchingResults();
    }



    private void scrapeJobPortalsAndDoAiMatching() throws InterruptedException {
        // simulate a unit of work
        LocalDateTime jobHardTimeLimit = LocalDateTime.now().plusHours(2);//Force stop if the job runs longer than 2 hour
        LocalDateTime currentTime = LocalDateTime.now();

        try {
            // Find all job cards by their attribute

            for (JobConfig jobConfig : appConfig.getJobConfigs()) {
                //Replace the seedUrl
                logger.info("Replacing seed URL to: {}", jobConfig.getInitUrl());
                jobScraper.updateContext(jobConfig);
                while (jobScraper.hasNextPage()) {
                    /*
                     * All code below is processing ONE PAGE of a job portal
                     */
                    if (currentTime.isAfter(jobHardTimeLimit)) {//hard stop after 2 hour
                        return;
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        logger.info("Job matching interrupted, stopping gracefully...");
                        return; // exit loop
                    }

                    List<WebElement> jobCardList = jobScraper.scrapeJobCardListing();

                    for (WebElement jobCardWebElement : jobCardList) {
                        JobPosting jobPosting = jobScraper.digestJobCard(jobCardWebElement);

                        if (isBannedJob(jobPosting)) continue;

                        Optional<JobPosting> jobPostingInDb = jobPostingRepository.findByJobId(jobPosting.getJobId());

                        if (jobPostingInDb.isEmpty()) {
                            jobPosting = jobScraper.scrapeJobDetails(jobPosting);
                            jobPostingRepository.save(jobPosting);
                        } else {
                            jobPosting = jobPostingInDb.get();
                            logger.info("Job already exists in the database: {}", jobPosting.getJobId());
                        }

                        queryOllamaAIs(jobPosting, jobConfig);
                    }

                    jobScraper.findNextPage();
                }
            }
        }catch(Exception e){
            logger.error("An expected error occurred during scraping: ", e);
        }

    }

}
