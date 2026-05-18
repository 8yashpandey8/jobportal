package com.jobportal.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String salary;
    private String experienceLevel;
    private String employmentType;
    private String status;
    private String recruiterName;
    private String companyName;
    private Set<String> skills;
    private LocalDateTime postedAt;
}
