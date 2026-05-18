package com.jobportal.service;

import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.JwtResponse;
import com.jobportal.dto.MessageResponse;
import com.jobportal.dto.SignupRequest;
import com.jobportal.entity.CandidateProfile;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.repository.CandidateProfileRepository;
import com.jobportal.repository.RecruiterProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtUtils;
import com.jobportal.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), role);
    }

    @Transactional
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        Role userRole;
        try {
            userRole = Role.valueOf(signUpRequest.getRole());
            if (userRole == Role.ROLE_ADMIN) {
                throw new RuntimeException("Error: Cannot register as ADMIN directly");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: Role is not valid.");
        }

        // Create new user's account
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(userRole)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        if (userRole == Role.ROLE_CANDIDATE) {
            CandidateProfile candidateProfile = CandidateProfile.builder()
                    .user(savedUser)
                    .firstName(signUpRequest.getFirstName())
                    .lastName(signUpRequest.getLastName())
                    .build();
            candidateProfileRepository.save(candidateProfile);
        } else if (userRole == Role.ROLE_RECRUITER) {
            RecruiterProfile recruiterProfile = RecruiterProfile.builder()
                    .user(savedUser)
                    .firstName(signUpRequest.getFirstName())
                    .lastName(signUpRequest.getLastName())
                    .companyName(signUpRequest.getCompanyName())
                    .build();
            recruiterProfileRepository.save(recruiterProfile);
        }

        return new MessageResponse("User registered successfully!");
    }
}
