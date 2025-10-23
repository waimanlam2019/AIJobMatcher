package site.raylambytes.aijobmatcher.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CTgoodjobsNextButtonPaginationStrategy implements PaginationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(CTgoodjobsNextButtonPaginationStrategy.class);

    @Override
    public boolean findNextPage(WebDriver driver, SeleniumScraperContext context) {
        if (context.getCurrentPage() >= context.getMaxPages()) {
            logger.info("Reached max pages ({}). Stopping.", context.getMaxPages());
            context.setHasNextPage(false);
            return false;
        }

        List<WebElement> pages = driver.findElements(By.cssSelector("ul.page-control a.btn--page"));

        for (int i = 0; i < pages.size(); i++) {
            WebElement page = pages.get(i);
            String classes = page.getAttribute("class");

            // Check if this page is the active one
            if (classes.contains("active")) {
                // Check that there *is* a next page
                if (i + 1 < pages.size()) {
                    WebElement nextPage = pages.get(i + 1);
                    String nextHref = nextPage.getAttribute("href");
                    logger.info("Next page URL: {}", nextHref);
                    context.setCurrentUrl(nextHref.startsWith("http") ? nextHref : "https://jobs.ctgoodjobs.hk/" + nextHref);
                    context.incrementPage();
                    return true;
                }
            }
        }

        logger.info("No next page link found.");
        context.setHasNextPage(false);
        return false;
    }
}