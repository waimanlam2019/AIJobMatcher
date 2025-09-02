package site.raylambytes.aijobmatcher.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class JobsDBNextButtonPaginationStrategy implements PaginationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(JobsDBNextButtonPaginationStrategy.class);

    @Override
    public boolean findNextPage(WebDriver driver, SeleniumScraperContext context) {
        if (context.getCurrentPage() >= context.getMaxPages()) {
            logger.info("Reached max pages ({}). Stopping.", context.getMaxPages());
            context.setHasNextPage(false);
            return false;
        }

        Optional<WebElement> nextLink = driver.findElements(By.cssSelector("a[data-automation='page-2']"))
                .stream().findFirst();

        if (nextLink.isEmpty()) {
            logger.info("No next page link found.");
            context.setHasNextPage(false);
            return false;
        }

        String url = nextLink.get().getAttribute("href");
        logger.info("Next page URL: {}", url);
        context.setInitUrl(url.startsWith("http") ? url : "https://hk.jobsdb.com" + url);
        context.incrementPage();
        return true;
    }
}