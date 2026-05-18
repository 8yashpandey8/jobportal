package com.jobportal.controller;

import com.jobportal.dto.JobResponse;
import com.jobportal.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<JobResponse>> getRecommendedJobs(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getRecommendedJobs(authentication.getName()));
    }
}
