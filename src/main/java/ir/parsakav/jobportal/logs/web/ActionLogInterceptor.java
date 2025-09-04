package ir.parsakav.jobportal.logs.web;

import ir.parsakav.jobportal.accounts.domain.ActionLog;
import ir.parsakav.jobportal.auth.security.UserPrincipal;
import ir.parsakav.jobportal.logs.repo.ActionLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Optional;

// logs/web/ActionLogInterceptor.java
@Component
@RequiredArgsConstructor
public class ActionLogInterceptor implements HandlerInterceptor {
    private final ActionLogRepository repo;
    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object h, Exception ex) {
        Long uid = null;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserPrincipal user) {
                uid = user.getId();
            }
        }

        repo.save(ActionLog.builder()
                .userId(uid)
                .method(req.getMethod())
                .path(req.getRequestURI())
                .ip(req.getRemoteAddr())
                .status(res.getStatus())
                .ts(Instant.now())
                .build());
    }

}
