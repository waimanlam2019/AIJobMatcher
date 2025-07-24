package site.raylambytes;

import org.openqa.selenium.WebElement;

import java.util.List;

public interface JobScraper {
    List<WebElement> scrapeJobCardListing();
    JobPosting digestJobCard(WebElement webElement);
    JobPosting scrapeJobDetails(JobPosting jobPosting);
}
