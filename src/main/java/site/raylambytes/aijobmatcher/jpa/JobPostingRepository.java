package site.raylambytes.aijobmatcher.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {

    // Custom query method: find by jobId
    Optional<JobPosting> findByJobId(String jobId);

    // Optional: delete by jobId
    void deleteByJobId(String jobId);

    // You can add more derived queries as needed, e.g.
    // List<JobPosting> findByCompany(String company);
}