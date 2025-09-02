package site.raylambytes.aijobmatcher.scraper;


public class SeleniumScraperContext {
    private String initUrl;
    private String currentUrl;
    private int currentPage;
    private int maxPages;
    private boolean hasNextPage;

    public SeleniumScraperContext() {
    }

    public String getInitUrl() { return initUrl; }
    public void setInitUrl(String initUrl) { this.initUrl = initUrl; }

    public String getCurrentUrl() {return currentUrl;}
    public void setCurrentUrl(String currentUrl) {this.currentUrl = currentUrl;}

    public int getCurrentPage() { return currentPage; }

    public void setCurrentPage(int currentPage) {this.currentPage = currentPage;}

    public void incrementPage() { this.currentPage++; }

    public int getMaxPages() { return maxPages; }
    public void setMaxPages(int maxPages) { this.maxPages = maxPages; }

    public boolean hasNextPage() { return hasNextPage; }
    public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }
}