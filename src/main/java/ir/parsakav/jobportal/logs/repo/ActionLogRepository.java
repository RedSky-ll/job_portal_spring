package ir.parsakav.jobportal.logs.repo;

import ir.parsakav.jobportal.accounts.domain.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Long> { }
