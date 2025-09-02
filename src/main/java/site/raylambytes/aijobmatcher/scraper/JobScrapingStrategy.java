package site.raylambytes.aijobmatcher.scraper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import site.raylambytes.aijobmatcher.jpa.JobPosting;

import java.util.List;

public interface JobScrapingStrategy {
    List<WebElement> scrapeJobCardListing(WebDriver driver, SeleniumScraperContext seleniumScraperContext);
    JobPosting digestJobCard(WebElement webElement);
    JobPosting scrapeDetails(WebDriver driver, JobPosting jobPosting);
}