package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String requirements;
    private String location;
    private String salary;
    private String experienceLevel;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    private Set<String> skills;
}
