package site.raylambytes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class RetryUtils {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);
    public static <T> T retry(int maxAttempts, long delayMillis, Supplier<T> action) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxAttempts) {
            try {
                return action.get();
            } catch (Exception e) {
                logger.info("Attempt " + (attempts + 1) + " failed: " + e.getMessage());
                lastException = e;
                attempts++;
                if (attempts < maxAttempts) {
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
            }
        }

        throw new RuntimeException("Retry failed after " + maxAttempts + " attempts", lastException);
    }

    public static void retryVoid(int maxAttempts, long delayMillis, Runnable action) {
        retry(maxAttempts, delayMillis, () -> {
            action.run();
            return null;
        });
    }
}
