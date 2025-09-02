package site.raylambytes.aijobmatcher.scraper;


public class SeleniumScraperContext {
    private String initUrl;
    private int currentPage;
    private int maxPages;
    private boolean hasNextPage;

    public SeleniumScraperContext(String initUrl, int maxPages) {
        this.initUrl = initUrl;
        this.maxPages = maxPages;
        this.currentPage = 1;
        this.hasNextPage = true;
    }

    public String getInitUrl() { return initUrl; }
    public void setInitUrl(String initUrl) { this.initUrl = initUrl; }

    public int getCurrentPage() { return currentPage; }
    public void incrementPage() { this.currentPage++; }

    public int getMaxPages() { return maxPages; }

    public boolean hasNextPage() { return hasNextPage; }
    public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }
}