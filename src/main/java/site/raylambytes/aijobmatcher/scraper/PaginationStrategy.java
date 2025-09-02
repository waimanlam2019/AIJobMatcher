package site.raylambytes.aijobmatcher.scraper;

import org.openqa.selenium.WebDriver;

public interface PaginationStrategy {
    boolean findNextPage(WebDriver driver, SeleniumScraperContext context);
}