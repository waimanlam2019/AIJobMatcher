package site.raylambytes.aijobmatcher.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchingResultRepository extends JpaRepository<MatchingResult, Integer> {

    Optional<MatchingResult> findByJobPostingAndAiModel(JobPosting jobPosting, String aiModel);
}
