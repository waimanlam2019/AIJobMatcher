package site.raylambytes.aijobmatcher.scraper;

import org.openqa.selenium.WebElement;
import site.raylambytes.aijobmatcher.jpa.JobPosting;

import java.util.List;

public interface JobScraper {
    List<WebElement> scrapeJobCardListing() throws InterruptedException;
    JobPosting digestJobCard(WebElement webElement);
    JobPosting scrapeJobDetails(JobPosting jobPosting);
    boolean hasNextPage();
    void findNextPage();

}
