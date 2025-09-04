package ir.parsakav.jobportal.jobs.api;

import ir.parsakav.jobportal.accounts.api.EmployerApplicationController;
import ir.parsakav.jobportal.accounts.domain.Application;
import ir.parsakav.jobportal.accounts.domain.Job;
import ir.parsakav.jobportal.accounts.service.ApplicationService;
import ir.parsakav.jobportal.jobs.dto.ApplicationDtos;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployerApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployerApplicationControllerTest {

    @Autowired MockMvc mvc;
    @MockBean
    ApplicationService applicationService;

    @Test
    void listForJob_returns_page() throws Exception {
        var a1 = new Application(); a1.setId(1L); a1.setStatus(Application.Status.SUBMITTED);
        var a2 = new Application(); a2.setId(2L); a2.setStatus(Application.Status.REVIEWED);
        Mockito.when(applicationService.employerListForJob(eq(100L), eq(10L), any()))
                .thenReturn(new PageImpl<>(List.of(a1, a2)));

        var user = SecurityMockMvcRequestPostProcessors.user("emp@x.com").roles("EMPLOYER");

        mvc.perform(get("/employer/jobs/10/applications").with(user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void decide_accept_ok() throws Exception {
        var app = new Application();
        app.setId(5L);
        Job j = new Job(); j.setId(10L); j.setTitle("Java Dev"); app.setJob(j);
        app.setStatus(Application.Status.ACCEPTED);

        Mockito.when(applicationService.decide(eq(100L), eq(5L), any(ApplicationDtos.DecisionRequest.class)))
                .thenReturn(app);

        var user = SecurityMockMvcRequestPostProcessors.user("emp@x.com").roles("EMPLOYER");

        mvc.perform(put("/employer/applications/5/decision")
                        .with(user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACCEPTED\",\"reason\":\"ok\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.jobId").value(10));
    }
}
