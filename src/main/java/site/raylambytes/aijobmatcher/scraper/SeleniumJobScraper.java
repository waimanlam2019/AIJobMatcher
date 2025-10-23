package site.raylambytes.aijobmatcher.scraper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.raylambytes.aijobmatcher.AppConfig;
import site.raylambytes.aijobmatcher.JobConfig;
import site.raylambytes.aijobmatcher.jpa.JobPosting;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class SeleniumJobScraper {
    private static final Logger logger = LoggerFactory.getLogger(SeleniumJobScraper.class);// for demo pu
    private final WebDriver listWebDriver;
    private final WebDriver detailWebDriver;
    private SeleniumScraperContext context;
    private PaginationStrategy paginationStrategy;
    private JobScrapingStrategy jobScrapingStrategy;

    private static final Random RANDOM = new Random();

    public SeleniumJobScraper(AppConfig appConfig) {
        this.paginationStrategy = new JobsDBNextButtonPaginationStrategy();
        this.jobScrapingStrategy = new JobsDBJobScrapingStrategy();
        //TODO revive this line later
        //WebDriverManager.chromedriver().setup();
        //TODO remove this line later
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\USER\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

        // Headless Chrome options


        ChromeOptions options = new ChromeOptions();

        //Force program to use custom chrome binary
        options.setBinary("C:\\Users\\USER\\Downloads\\chrome-win64\\chrome-win64\\chrome.exe");


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

        String randomUserAgent = userAgents.get(RANDOM.nextInt(userAgents.size()));
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

    //Left search result panel from jobsdb
    //return the raw html element
    public List<WebElement> scrapeJobCardListing(){
        return jobScrapingStrategy.scrapeJobCardListing(listWebDriver, context);
    }

    public JobPosting digestJobCard(WebElement webElement) {
        return jobScrapingStrategy.digestJobCard(webElement);
    }

    public JobPosting scrapeJobDetails(JobPosting jobPosting) {
        return jobScrapingStrategy.scrapeDetails(detailWebDriver, jobPosting);
    }

    public void findNextPage(){
        paginationStrategy.findNextPage(listWebDriver, context);
    }

    public boolean hasNextPage(){
        return context.hasNextPage();
    }

    public void updateContext(JobConfig jobConfig){
        this.context = new SeleniumScraperContext();
        context.setInitUrl(jobConfig.getInitUrl());
        context.setCurrentUrl(jobConfig.getInitUrl());
        context.setCurrentPage(1);
        context.setMaxPages(jobConfig.getMaxPages());
        context.setHasNextPage(true);

        URL url = null;
        try {
            url = new URL(jobConfig.getInitUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String host = url.getHost().toLowerCase();

        if (host.contains("ctgoodjobs.hk")) {
            this.paginationStrategy = new CTgoodjobsNextButtonPaginationStrategy();
            this.jobScrapingStrategy = new CTGoodJobsJobScrapingStrategy();
        }else { //jobsdb
            this.paginationStrategy = new JobsDBNextButtonPaginationStrategy();
            this.jobScrapingStrategy = new JobsDBJobScrapingStrategy();
        }
    }

}