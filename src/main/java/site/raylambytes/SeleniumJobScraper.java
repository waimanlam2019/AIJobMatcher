package site.raylambytes;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeleniumJobScraper implements JobScraper {
    private static final Logger logger = LoggerFactory.getLogger(SeleniumJobScraper.class);// for demo pu
    private final WebDriver listWebDriver;
    private final WebDriver detailWebDriver;
    private final String baseUrl;

    public SeleniumJobScraper(WebDriver listWebDriver, WebDriver detailWebDriver, String baseUrl) {
        this.listWebDriver = listWebDriver;
        this.detailWebDriver = detailWebDriver;
        this.baseUrl = baseUrl;
    }

    //Left search result panel from jobsdb
    //return the raw html element
    @Override
    public List<WebElement> scrapeJobCardListing() {
        logger.info("Retrieving job cards from: {}", baseUrl);
        RetryUtils.retryVoid(3, 2000, () -> listWebDriver.get(baseUrl));

        // Wait for jobs to load (basic sleep; for production use WebDriverWait)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<WebElement> jobList = listWebDriver.findElements(By.cssSelector("article[data-card-type=JobCard]"));
        logger.info("Found {} job(s) on jobsdb link.", jobList.size());
        if (jobList.isEmpty()) {
            logger.info("❌ No job found. Exiting.");
            return new ArrayList<WebElement>();
        }
        return jobList;
    }

    @Override
    public JobPosting digestJobCard(WebElement webElement) {
        logger.info("Processing job card...");
        String title = tryFindOptionalElement(webElement, By.cssSelector("a[data-automation=jobTitle]"))
                .map(WebElement::getText).orElse("Unknown");
        String company = tryFindOptionalElement(webElement, By.cssSelector("a[data-automation=jobCompany]"))
                .map(WebElement::getText).orElse("Unknown");
        String location = tryFindOptionalElement(webElement, By.cssSelector("span[data-automation=jobLocation]"))
                .map(WebElement::getText).orElse("Unknown");
        String jobUrl = tryFindOptionalElement(webElement, By.cssSelector("a[data-automation=jobTitle]"))
                .map(e->(e.getAttribute("href"))).orElse("Unknown");
        String jobId = webElement.getAttribute("data-job-id");

        logger.info("\uD83D\uDD17 Job Id: {}", jobId);
        logger.info("\uD83D\uDD39 Title: {}", title);//Check
        logger.info("\uD83C\uDFE2 Company: {}", company);//Check
        logger.info("\uD83D\uDCCD Location: {}", location);
        logger.info("\uD83D\uDD17 URL: {}", jobUrl);

        JobPosting jobPosting = new JobPosting();
        jobPosting.setJobId(jobId);
        jobPosting.setTitle(title);
        jobPosting.setCompany(company);
        jobPosting.setLocation(location);
        jobPosting.setUrl(jobUrl);
        return jobPosting;
    }

    @Override
    public JobPosting scrapeJobDetails(JobPosting jobPosting) {
        logger.info("Retrieving job details from: {}", jobPosting.getUrl());
        RetryUtils.retryVoid(3, 2000, () -> detailWebDriver.get(jobPosting.getUrl()));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Full Time or Part Time
        String jobType = tryFindOptionalElement(detailWebDriver, By.cssSelector("span[data-automation='job-detail-work-type'] a"))
                .map(WebElement::getText)
                .orElse("Unknown");
        logger.info("Job Type: {}", jobType);

        Optional<WebElement> jobDescDiv = tryFindOptionalElement(detailWebDriver, By.cssSelector("div[data-automation='jobAdDetails']"));

        if ( jobDescDiv.isPresent() ) {
            // Get the full HTML inside the job description container
            String jobDescriptionHtml = jobDescDiv.get().getAttribute("innerHTML");

            // Or get only the visible text (stripped of tags)
            String jobDescriptionText = jobDescDiv.get().getText();
            logger.info("Job Description: {}", jobDescriptionText);
            jobPosting.setDescription(jobDescriptionText);
            jobPosting.setDescriptionHtml(jobDescriptionHtml);
        }
        return jobPosting;
    }

    public Optional<WebElement> tryFindOptionalElement(WebElement parent, By selector) {
        try {
            return Optional.of(parent.findElement(selector));
        } catch (NoSuchElementException e) {
            logger.warn("Element not found using selector: {}", selector);
            return Optional.empty();
        }
    }

    public Optional<WebElement> tryFindOptionalElement(WebDriver driver, By selector) {
        try {
            return Optional.of(driver.findElement(selector));
        } catch (NoSuchElementException e) {
            logger.warn("Element not found using selector: {}", selector);
            return Optional.empty();
        }
    }
}