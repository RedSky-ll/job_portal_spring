package ir.parsakav.jobportal.jobs.api;

import ir.parsakav.jobportal.accounts.api.ApplicationController;
import ir.parsakav.jobportal.accounts.domain.Application;
import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.accounts.service.ApplicationService;
import ir.parsakav.jobportal.jobs.dto.ApplicationDtos;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
class ApplicationControllerTest {

    @Autowired MockMvc mvc;
    @MockBean
    ApplicationService applicationService;

    @Test
    void apply_returns_200_with_response() throws Exception {
        var app = new Application();
        app.setId(111L);
        Job j = new Job(); j.setId(10L); j.setTitle("Java Dev");
        app.setJob(j);
        app.setStatus(Application.Status.SUBMITTED);

        Mockito.when(applicationService.apply(eq(10L), eq(200L), any(ApplicationDtos.ApplyRequest.class)))
                .thenReturn(app);

        // شبیه‌سازی کاربر SEEKER با id=200
        var user = SecurityMockMvcRequestPostProcessors.user("seek@x.com").roles("SEEKER");
        // چون ما در کنترلر از Authentication.getPrincipal به UserPrincipal cast می‌کنیم،
        // فیلتر امنیتی را غیرفعال کردیم و فقط مسیر را آزمایش می‌کنیم.

        mvc.perform(post("/jobs/10/apply")
                        .with(user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coverLetter\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(10));
    }
}
