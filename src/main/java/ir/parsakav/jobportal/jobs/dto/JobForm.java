package ir.parsakav.jobportal.jobs.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class JobForm {
    @NotBlank private String title;
    @NotBlank private String description;
    private String location;
    private String employmentType;
    private List<String> tags;
}
