package site.raylambytes.aijobmatcher.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.raylambytes.aijobmatcher.jpa.JobPosting;
import site.raylambytes.aijobmatcher.util.RetryUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CTGoodJobsJobScrapingStrategy implements JobScrapingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(CTGoodJobsJobScrapingStrategy.class);

    @Override
    public List<WebElement> scrapeJobCardListing(WebDriver listWebDriver, SeleniumScraperContext context) {
        String currentUrl = context.getCurrentUrl();
        logger.info("Retrieving job cards from: {}", currentUrl);
        RetryUtils.retryVoid(3, 2000, () -> listWebDriver.get(currentUrl));

        WebDriverWait wait = new WebDriverWait(listWebDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.job-list-w")));
        List<WebElement> jobList = listWebDriver.findElements(By.cssSelector("div.job-card"));
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

        WebDriver driver = ((RemoteWebDriver) ((RemoteWebElement) webElement).getWrappedDriver());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(webElement, By.cssSelector("a.jc-position > h2")));

        String title = tryFindOptionalElement(webElement, By.cssSelector("a.jc-position > h2"))
                .map(WebElement::getText).orElse("Unknown");
        String company = tryFindOptionalElement(webElement, By.cssSelector("a.jc-company"))
                .map(WebElement::getText).orElse("Unknown");
        String location = tryFindOptionalElement(webElement, By.cssSelector("div.row.jc-info > div.col-12"))
                .map(WebElement::getText).orElse("Unknown");
        String jobUrl = tryFindOptionalElement(webElement, By.cssSelector("a.jc-position"))
                .map(e->(e.getAttribute("href"))).orElse("Unknown");
        String jobId = webElement.getAttribute("data-job-id");

        logger.info("\uD83D\uDD17 Job Id: {}", jobId);
        logger.info("\uD83D\uDD39 Title: {}", title);//Check
        logger.info("\uD83C\uDFE2 Company: {}", company);//Check
        logger.info("\uD83D\uDCCD Location: {}", location);
        logger.info("\uD83D\uDD17 URL: {}", jobUrl);

        JobPosting jobPosting = new JobPosting();
        jobPosting.setJobId(jobId);
        jobPosting.setSource("CTgoodjobs");
        jobPosting.setTitle(title);
        jobPosting.setCompany(company);
        jobPosting.setLocation(location);
        jobPosting.setUrl(jobUrl);
        return jobPosting;
    }

    @Override
    public JobPosting scrapeDetails(WebDriver detailWebDriver, JobPosting jobPosting) {
        logger.info("Retrieving job details from: {}", jobPosting.getUrl());
        RetryUtils.retryVoid(3, 2000, () -> detailWebDriver.get(jobPosting.getUrl()));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore the flag
            logger.warn("Scraper interrupted while sleeping, exiting early...");
            return jobPosting; // or break out gracefully
        }

        //Full Time or Part Time
        //TODO it seems there is not full time or part time info on CTgoodjobs
        /*String jobType = tryFindOptionalElement(detailWebDriver,
                By.cssSelector("span[data-automation='job-detail-work-type'], span[data-automation='job-detail-work-type'] a"))
                .map(WebElement::getText)
                .orElse("Unknown");
        logger.info("Job Type: {}", jobType);*/
        jobPosting.setJobType("Unknown");//

        Optional<WebElement> jobDescDiv = tryFindOptionalElement(detailWebDriver, By.cssSelector("div.jd__sec.jd__desc"));

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