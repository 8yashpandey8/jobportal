package com.jobportal.service;

import com.jobportal.dto.JobResponse;
import com.jobportal.entity.*;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CandidateProfileRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterProfileRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public List<JobResponse> getRecommendedJobs(String candidateEmail) {
        User candidateUser = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        CandidateProfile profile = candidateProfileRepository.findByUser(candidateUser)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        List<Job> allJobs = jobRepository.findAll();

        return allJobs.stream()
                .filter(job -> job.getStatus() == JobStatus.OPEN)
                .filter(job -> {
                    if (profile.getSkills() == null || profile.getSkills().isEmpty()) return true; // If no skills, return all
                    // Check if job has at least one matching skill
                    return job.getSkills().stream().anyMatch(profile.getSkills()::contains);
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private JobResponse mapToResponse(Job job) {
        RecruiterProfile profile = recruiterProfileRepository.findByUser(job.getRecruiter()).orElse(null);
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salary(job.getSalary())
                .experienceLevel(job.getExperienceLevel())
                .employmentType(job.getEmploymentType().name())
                .status(job.getStatus().name())
                .recruiterName(profile != null ? profile.getFirstName() + " " + profile.getLastName() : "Unknown")
                .companyName(profile != null ? profile.getCompanyName() : "Unknown")
                .skills(job.getSkills().stream().map(Skill::getName).collect(Collectors.toSet()))
                .postedAt(job.getCreatedAt())
                .build();
    }
}
