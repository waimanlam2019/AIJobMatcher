package site.raylambytes.aijobmatcher.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.raylambytes.aijobmatcher.AppConfig;
import site.raylambytes.aijobmatcher.util.RetryUtils;
import site.raylambytes.aijobmatcher.jpa.JobPosting;

import java.util.*;

@Service
public class SeleniumJobScraper implements JobScraper {
    private static final Logger logger = LoggerFactory.getLogger(SeleniumJobScraper.class);// for demo pu
    private final WebDriver listWebDriver;
    private final WebDriver detailWebDriver;

    private String initUrl;
    private boolean hasNextPage = true;
    private int currentPage = 1;
    private int maxPages = 3;

    public SeleniumJobScraper(AppConfig appConfig) {
        this.initUrl = appConfig.getInitUrl();
        this.maxPages = Integer.parseInt(appConfig.getMaxPages());
        WebDriverManager.chromedriver().setup();

        // Headless Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // new headless mode for Chrome > 109
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        List<String> userAgents = Arrays.asList(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.1 Safari/605.1.15",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 16_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15E148 Safari/604.1"
        );
        Random rand = new Random();
        String randomUserAgent = userAgents.get(rand.nextInt(userAgents.size()));
        options.addArguments("user-agent="+randomUserAgent);

        // ✅ Disguise automation
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        System.setProperty("webdriver.chrome.driver", appConfig.getChromeDriverPath());

        listWebDriver = new ChromeDriver(options);
        detailWebDriver = new ChromeDriver(options);
        // ✅ More stealth: remove webdriver property using JS
        ((ChromeDriver) listWebDriver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        ((ChromeDriver) detailWebDriver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");


    }

    public String getInitUrl() {
        return initUrl;
    }

    public void setInitUrl(String initUrl) {
        this.initUrl = initUrl;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    //Left search result panel from jobsdb
    //return the raw html element
    @Override
    public List<WebElement> scrapeJobCardListing() {
        logger.info("Retrieving job cards from: {}", initUrl);
        RetryUtils.retryVoid(3, 2000, () -> listWebDriver.get(initUrl));

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
        jobPosting.setJobType(jobType);

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

    @Override
    public void findNextPage(){
        if ( currentPage > this.maxPages ){
            logger.info("Max page page reached. Stop scraping.");
            this.setHasNextPage(false);
            return;
        }

        // Find the "Next" button link using a reliable selector
        logger.info("Finding next page link...");
        Optional<WebElement> nextLink = tryFindOptionalElement(listWebDriver, By.cssSelector("a[data-automation='page-2']"));


        if (nextLink.isPresent()){
            logger.info("Found next page link.");
            // Extract the href attribute
            String relativeUrl = nextLink.get().getAttribute("href");

            // If it's a relative URL, convert to absolute
            String absoluteUrl = relativeUrl;
            if (!relativeUrl.startsWith("http")) {
                absoluteUrl = "https://hk.jobsdb.com" + relativeUrl;
            }

            logger.info("Url of next page: {}", absoluteUrl);
            this.setInitUrl(absoluteUrl);
            this.setHasNextPage(true);
            currentPage++;
        }else{
            this.setHasNextPage(false);
        }

    }


}