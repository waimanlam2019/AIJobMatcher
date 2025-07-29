package site.raylambytes.aijobmatcher.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matching_results")
public class MatchingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto increment
    private Integer id;

    // Establish proper foreign key to JobPosting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Column(name = "ai_model", nullable = false)
    private String aiModel;

    @Column(columnDefinition = "text")
    private String verdict;

    @Column(name = "shortlist_flag")
    private boolean shortlistFlag;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public boolean isShortlistFlag() {
        return shortlistFlag;
    }

    public void setShortlistFlag(boolean shortlistFlag) {
        this.shortlistFlag = shortlistFlag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
