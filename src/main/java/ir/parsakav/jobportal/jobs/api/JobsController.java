package ir.parsakav.jobportal.jobs.api;

import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.jobs.service.JobQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobsController {
    private final JobQueryService jobQuery;

    @GetMapping
    public Page<Job> search(@RequestParam(defaultValue = "") String q,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        return jobQuery.search(q, page, size);
    }

    @GetMapping("/{id}")
    public Job byId(@PathVariable Long id) {
        // ساده‌سازی: ریپو مستقیم
        return jobQuery.getById(id);
    }


}
