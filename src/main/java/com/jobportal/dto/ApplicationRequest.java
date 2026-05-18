package com.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private Long resumeId; // optional if they don't have multiple
    
    private String coverLetter;
}
