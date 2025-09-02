package site.raylambytes.aijobmatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

//@SpringBootApplication
public class AIJobMatcherBatch implements CommandLineRunner {

    @Autowired
    private AIJobMatcherService aiJobMatcherService;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private AppConfig appConfig;

    public static void main(String[] args) {
        SpringApplication.run(AIJobMatcherBatch.class, args);
    }

    @Override
    public void run(String... args) {
        if (appConfig.isOfflineMode()){
            aiJobMatcherService.runMatchingOffline();
        }else{
            aiJobMatcherService.startJobMatching();
        }

    }
}