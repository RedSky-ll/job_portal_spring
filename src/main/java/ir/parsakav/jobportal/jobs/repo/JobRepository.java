package ir.parsakav.jobportal.jobs.repo;



import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.accounts.domain.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("select j from Job j where j.approved=true and lower(j.title) like lower(concat('%', ?1, '%'))")
    Page<Job> findByTitleContainingIgnoreCaseAndApprovedTrue(String q, Pageable pageable);
    // ir/parsakav/jobportal/jobs/repo/JobRepository.java
    List<Job> findByApprovedFalse();

    Page<Job> findByEmployer(User employer, Pageable pageable);
}
