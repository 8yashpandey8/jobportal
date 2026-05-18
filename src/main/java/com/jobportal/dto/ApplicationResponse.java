package com.jobportal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String candidateName;
    private String candidateEmail;
    private String resumeUrl;
    private String status;
    private String coverLetter;
    private LocalDateTime appliedAt;
}
