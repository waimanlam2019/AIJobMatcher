package site.raylambytes.aijobmatcher;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.raylambytes.aijobmatcher.ai.AIClient;
import site.raylambytes.aijobmatcher.jpa.*;
import site.raylambytes.aijobmatcher.scraper.JobScraper;
import site.raylambytes.aijobmatcher.util.EmailNotifier;
import site.raylambytes.aijobmatcher.util.PromptBuilder;
import site.raylambytes.aijobmatcher.util.ResponseAnalyzer;
import site.raylambytes.aijobmatcher.util.RetryUtils;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AIJobMatcherService {
    private static final Logger logger = LoggerFactory.getLogger(AIJobMatcherService.class);// for demo purposes

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
    private EmailNotifier emailNotifier;

    @Autowired
    private AIClient aiClient;

    @Autowired
    private JobScraper jobScraper;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private MatchingResultRepository matchingResultRepository;

    public void runMatching() {
        // Logic for matching jobs with AI
        LocalDateTime jobHardTimeLimit = LocalDateTime.now().plusHours(1);//Force stop if the job runs longer than 1 hour
        LocalDateTime currentTime = LocalDateTime.now();

        try {
            // Find all job cards by their attribute
            while(jobScraper.hasNextPage() && currentTime.isBefore(jobHardTimeLimit)) { //default only scrape 3 pages and hard stop after 1 hour
                List<WebElement> jobCardList = jobScraper.scrapeJobCardListing();

                jobCardList.stream()
                        .limit(Integer.parseInt(appConfig.getMaxJobsPerPage())) //Can limit the program to process less job to speed up testing next page logic
                        .forEach(jobCardWebElement -> {
                            JobPosting jobPosting = jobScraper.digestJobCard(jobCardWebElement);

                            String title = jobPosting.getTitle().toLowerCase();
                            boolean isBanned = bannedJobs.stream()
                                    .anyMatch(keyword -> title.contains(keyword.toLowerCase())); // to handle both English & Chinese

                            if (isBanned) {
                                logger.info("Filtered out unsuitable job: {}", title);
                                return;
                            }

                            Optional<JobPosting> jobPostingInDb = jobPostingRepository.findByJobId(jobPosting.getJobId());

                            if (jobPostingInDb.isEmpty()) {
                                jobPosting = jobScraper.scrapeJobDetails(jobPosting);
                                jobPostingRepository.save(jobPosting);
                            } else {
                                jobPosting = jobPostingInDb.get();
                                logger.info("Job already exists in the database: {}", jobPosting.getJobId());
                            }

                            Optional<MatchingResult> matchingResultInDb = matchingResultRepository.findByJobPostingAndAiModel(jobPosting, aiClient.getAiModel());
                            if ( matchingResultInDb.isEmpty() || appConfig.isRematch() ){
                                String aiRoleplay = appConfig.getAiRoleplay();
                                String candidateProfile = appConfig.getCandidateProfile();
                                String aiTask = appConfig.getAiTask();
                                PromptBuilder promptBuilder = new PromptBuilder(aiRoleplay, candidateProfile, jobPosting.getDescription(), aiTask);
                                String prompt = promptBuilder.buildPrompt();

                                logger.info("Prompt: {}", prompt);
                                logger.info("Token usage estimate: {}", aiClient.estimateTokenUsage(prompt));

                                String suggestion = aiClient.query(prompt);

                                // Print it nicely
                                logger.info("Ollama Suggestion:\n{}", suggestion.trim());
                                logger.info("Token usage estimate: {}", aiClient.estimateTokenUsage(suggestion));


                                boolean shortlistFlag = ResponseAnalyzer.isJobGoodToApply(suggestion);
                                MatchingResult matchingResult = new MatchingResult();
                                matchingResult.setJobPosting(jobPosting);
                                matchingResult.setJobId(jobPosting.getJobId());
                                matchingResult.setAiModel(aiClient.getAiModel());
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
                            }else{
                                logger.info("Skip matching because matching result already exists for job: {}", jobPosting.getJobId());
                            }


                        });
                jobScraper.findNextPage();
            }
        } catch (Exception e) {
            logger.error("An expected error occurred during scraping: ", e);
        }
    }
    public List<JobMatchingResultDTO> getAllJobMatchingResults() {
        return jobPostingRepository.findAllWithMatchingResults();
    }

}
