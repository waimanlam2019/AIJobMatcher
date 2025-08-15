package site.raylambytes.aijobmatcher.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {

    // Custom query method: find by jobId
    Optional<JobPosting> findByJobId(String jobId);

    List<JobPosting> findAllByOrderByIdDesc();

    List<JobPosting> findAllByJobTypeInOrderByIdDesc(List<String> jobTypes);

    // Optional: delete by jobId
    void deleteByJobId(String jobId);

    @Query("""
SELECT new site.raylambytes.aijobmatcher.jpa.JobMatchingResultDTO(
    j.title, j.company, j.location, j.jobType, j.description, j.url,
    m.aiModel, m.verdict, m.shortlistFlag, m.createdAt
)
FROM JobPosting j 
JOIN MatchingResult m ON j.id = m.jobPosting.id
WHERE j.id IN (
    SELECT mr.jobPosting.id
    FROM MatchingResult mr
    WHERE mr.shortlistFlag = true
    GROUP BY mr.jobPosting.id
    HAVING COUNT(mr.id) >= 4
)
ORDER BY j.id DESC, m.aiModel
""")
    List<JobMatchingResultDTO> findAllWithMatchingResults();


}