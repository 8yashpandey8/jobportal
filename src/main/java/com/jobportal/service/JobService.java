package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.entity.*;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterProfileRepository;
import com.jobportal.repository.SkillRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public JobResponse createJob(JobRequest jobRequest, String recruiterEmail) {
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RecruiterProfile profile = recruiterProfileRepository.findByUser(recruiter)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));

        Set<Skill> skills = new HashSet<>();
        if (jobRequest.getSkills() != null) {
            for (String skillName : jobRequest.getSkills()) {
                Skill skill = skillRepository.findByName(skillName.toLowerCase())
                        .orElseGet(() -> skillRepository.save(Skill.builder().name(skillName.toLowerCase()).build()));
                skills.add(skill);
            }
        }

        Job job = Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .requirements(jobRequest.getRequirements())
                .location(jobRequest.getLocation())
                .salary(jobRequest.getSalary())
                .experienceLevel(jobRequest.getExperienceLevel())
                .employmentType(EmploymentType.valueOf(jobRequest.getEmploymentType()))
                .status(JobStatus.OPEN)
                .recruiter(recruiter)
                .skills(skills)
                .build();

        Job savedJob = jobRepository.save(job);
        return mapToResponse(savedJob, profile);
    }

    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream().map(job -> {
            RecruiterProfile profile = recruiterProfileRepository.findByUser(job.getRecruiter()).orElse(null);
            return mapToResponse(job, profile);
        }).collect(Collectors.toList());
    }

    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        RecruiterProfile profile = recruiterProfileRepository.findByUser(job.getRecruiter()).orElse(null);
        return mapToResponse(job, profile);
    }

    @Transactional
    public void deleteJob(Long id, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getEmail().equals(recruiterEmail)) {
            throw new RuntimeException("Not authorized to delete this job");
        }

        jobRepository.delete(job);
    }

    private JobResponse mapToResponse(Job job, RecruiterProfile profile) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
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
