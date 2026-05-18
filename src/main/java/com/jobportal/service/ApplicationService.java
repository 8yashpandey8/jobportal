package com.jobportal.service;

import com.jobportal.dto.ApplicationRequest;
import com.jobportal.dto.ApplicationResponse;
import com.jobportal.entity.*;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    @Transactional
    public ApplicationResponse applyForJob(ApplicationRequest request, String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (applicationRepository.findByJobAndCandidate(job, candidate).isPresent()) {
            throw new RuntimeException("You have already applied for this job");
        }

        Resume resume = null;
        if (request.getResumeId() != null) {
            resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        } else {
            // Find default resume
            List<Resume> resumes = resumeRepository.findByCandidate(candidate);
            if (!resumes.isEmpty()) {
                resume = resumes.stream().filter(Resume::isDefault).findFirst().orElse(resumes.get(0));
            }
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .candidate(candidate)
                .resume(resume)
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.APPLIED)
                .build();

        JobApplication saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    public List<ApplicationResponse> getCandidateApplications(String email) {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        return applicationRepository.findByCandidate(candidate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponse> getJobApplications(Long jobId, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getRecruiter().getEmail().equals(recruiterEmail)) {
            throw new RuntimeException("Unauthorized access to applications");
        }

        return applicationRepository.findByJob(job).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse updateApplicationStatus(Long id, String statusStr, String recruiterEmail) {
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJob().getRecruiter().getEmail().equals(recruiterEmail)) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        ApplicationStatus status;
        try {
            status = ApplicationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status");
        }

        application.setStatus(status);
        return mapToResponse(applicationRepository.save(application));
    }

    private ApplicationResponse mapToResponse(JobApplication app) {
        CandidateProfile candidateProfile = candidateProfileRepository.findByUser(app.getCandidate()).orElse(null);
        String candName = candidateProfile != null ? candidateProfile.getFirstName() + " " + candidateProfile.getLastName() : "Unknown";
        
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .companyName(app.getJob().getRecruiter().getEmail()) // Simplify for now
                .candidateName(candName)
                .candidateEmail(app.getCandidate().getEmail())
                .resumeUrl(app.getResume() != null ? app.getResume().getFileUrl() : null)
                .status(app.getStatus().name())
                .coverLetter(app.getCoverLetter())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
