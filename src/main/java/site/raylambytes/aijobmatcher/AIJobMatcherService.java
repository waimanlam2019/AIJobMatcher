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
        LocalDateTime oneHourLater = LocalDateTime.now().plusHours(1);
        LocalDateTime currentTime = LocalDateTime.now();

        try {
            // Find all job cards by their attribute
            while(jobScraper.hasNextPage() && currentTime.isBefore(oneHourLater)) { //default only scrape 3 pages and hard stop after 1 hour
                List<WebElement> jobCardList = jobScraper.scrapeJobCardListing();

                jobCardList.stream()
                        .limit(Integer.parseInt(appConfig.getMaxJobs()))
                        .forEach(jobCard -> {
                            JobPosting jobPosting = jobScraper.digestJobCard(jobCard);
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
                                String suggestion = aiClient.query(prompt);

                                // Print it nicely
                                logger.info("Ollama Suggestion:\n{}", suggestion.trim());

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


                                if (shortlistFlag) {
                                    String subject = "💼 New Job Match Found!" + jobPosting.getTitle() + " at " + jobPosting.getCompany();
                                    String body = "Title: " + jobPosting.getTitle() + "<br/>" + "Company: " + jobPosting.getCompany() + "<br/>" + jobPosting.getUrl() + "<br/><br/>You might be a good fit for this job:<br/>";
                                    String jobDescriptionStyle = "<div style=\"border: 2px solid #333333; padding: 15px; border-radius: 6px; margin-bottom: 20px;\">";
                                    body += jobDescriptionStyle + jobPosting.getDescriptionHtml();
                                    String aiSuggestionStyle = "<div style=\"background-color: #f0f4f8; border-left: 4px solid #3b82f6; padding: 15px; margin-top: 20px; font-family: monospace; white-space: pre-wrap;line-height: 1.5;\">";
                                    body += "</div><br/>AI says<br/>" + aiSuggestionStyle + "<pre>" + suggestion.trim() + "</pre></div>";

                                    final String emailBody = body;
                                    RetryUtils.retryVoid(3, 3000, () -> {
                                        emailNotifier.sendEmail(subject, emailBody);
                                    });
                                }
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
