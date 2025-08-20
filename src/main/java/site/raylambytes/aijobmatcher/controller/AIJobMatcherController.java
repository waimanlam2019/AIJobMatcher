package site.raylambytes.aijobmatcher.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.raylambytes.aijobmatcher.AIJobMatcherService;
import site.raylambytes.aijobmatcher.jpa.JobMatchingResultDTO;
import site.raylambytes.aijobmatcher.scraper.SeleniumJobScraper;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AIJobMatcherController {
    private static final Logger logger = LoggerFactory.getLogger(AIJobMatcherController.class);

    @Autowired
    private AIJobMatcherService aiJobMatcherService;
    // GET /api/status
    @GetMapping("/matchingresults")
    public ResponseEntity<List<JobMatchingResultDTO>> getMatchingResults() {
        logger.info("Received GET request for matching results");
        List<JobMatchingResultDTO> results = aiJobMatcherService.getAllJobMatchingResults();
        return ResponseEntity.ok(results);
    }

    // POST /api/jobmatch
    @PostMapping("/startjobmatching")
    public ResponseEntity<String> doJobMatching() {
        logger.info("Received POST request to start job matching");
        aiJobMatcherService.runMatching();
        logger.info("Job matching process finished successfully");
        return ResponseEntity.ok("Received POST request. You handle it from here, boss.");
    }

    @PostMapping("/stopjobmatching")
    public ResponseEntity<String> stop() {
        logger.info("Received POST request to stop job matching");
        aiJobMatcherService.stopJobMatching();
        logger.info("Job matching process stopped");
        return ResponseEntity.ok("Job matching stopped");
    }
}
