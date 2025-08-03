package site.raylambytes.aijobmatcher.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.raylambytes.aijobmatcher.AIJobMatcherService;
import site.raylambytes.aijobmatcher.jpa.JobMatchingResultDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AIJobMatcherController {

    @Autowired
    private AIJobMatcherService aiJobMatcherService;
    // GET /api/status
    @GetMapping("/matchingresults")
    public ResponseEntity<List<JobMatchingResultDTO>> getMatchingResults() {
        List<JobMatchingResultDTO> results = aiJobMatcherService.getAllJobMatchingResults();
        return ResponseEntity.ok(results);
    }

    // POST /api/jobmatch
    @PostMapping("/dojobmatching")
    public ResponseEntity<String> doJobMatching(@RequestBody String requestBody) {
        aiJobMatcherService.runMatching();
        return ResponseEntity.ok("Received POST request. You handle it from here, boss.");
    }
}
