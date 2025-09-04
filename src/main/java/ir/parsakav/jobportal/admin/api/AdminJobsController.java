package ir.parsakav.jobportal.admin.api;
import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.jobs.repo.JobRepository;
import ir.parsakav.jobportal.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/admin/jobs")
@RequiredArgsConstructor
public class AdminJobsController {
    private final JobRepository jobs;
    private final NotificationService notifs;

    // همه‌ی جاب‌های تاییدنشده
    @GetMapping("/pending")
    public List<Job> listPending() {
        return jobs.findByApprovedFalse();
    }


    @PutMapping("/{id}/approve")
    public Job approve(@PathVariable Long id){
        Job j = jobs.findById(id).orElseThrow();
        j.setApproved(true);
        jobs.save(j);
        notifs.push(j.getEmployer().getId(), "Job approved", "Your job \""+j.getTitle()+"\" is now live.");
        return j;
    }
}
