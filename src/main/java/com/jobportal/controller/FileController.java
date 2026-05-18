package com.jobportal.controller;

import com.jobportal.dto.MessageResponse;
import com.jobportal.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload-resume")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<MessageResponse> uploadResume(@RequestParam("file") MultipartFile file, Authentication authentication) {
        fileStorageService.storeFile(file, authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Resume uploaded successfully"));
    }
}
