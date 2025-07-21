package com.project.personalfinancemanager.service;

import com.project.personalfinancemanager.dto.UserProfileDTO;
import com.project.personalfinancemanager.entity.UserProfileEntity;
import com.project.personalfinancemanager.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final EmailService emailService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${activation.token.endpoint}")
    private String activationEndpoint;

    @Value("http://localhost:8080")
    private String baseUrl;

    public UserProfileDTO registerProfile(UserProfileDTO userprofileDTO){
        UserProfileEntity newUserProfileEntity = toEntity(userprofileDTO);
        newUserProfileEntity.setActivationToken(UUID.randomUUID().toString());
        newUserProfileEntity = userProfileRepository.save(newUserProfileEntity);
        String activationLink = baseUrl+contextPath+activationEndpoint+newUserProfileEntity.getActivationToken();
        String subject = "Activate you Personal Finance Manager account";
        String body = "Click on the following link to activate your account: " + activationLink;
        emailService.sendEmail(newUserProfileEntity.getEmail(), subject,body);
        return toDTO(newUserProfileEntity);
    }

    public boolean activateProfile(String activationToken) {
        return userProfileRepository.findByActivationToken(activationToken)
                .map(profile->{
                    profile.setIsActive(true);
                    profile.setActivationToken(null);
                    userProfileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public UserProfileEntity toEntity(UserProfileDTO userProfileDTO){
        return UserProfileEntity.builder()
                .id(userProfileDTO.getId())
                .fullName(userProfileDTO.getFullName())
                .email(userProfileDTO.getEmail())
                .password(userProfileDTO.getPassword())
                .profileImageUrl(userProfileDTO.getProfileImageUrl())
                .createdAt(userProfileDTO.getCreatedAt())
                .updatedAt(userProfileDTO.getUpdatedAt())
                .build();
    }

    public UserProfileDTO toDTO(UserProfileEntity userProfileEntity){
        return UserProfileDTO.builder()
                .id(userProfileEntity.getId())
                .fullName(userProfileEntity.getFullName())
                .email(userProfileEntity.getEmail())
                .profileImageUrl(userProfileEntity.getProfileImageUrl())
                .createdAt(userProfileEntity.getCreatedAt())
                .updatedAt(userProfileEntity.getUpdatedAt())
                .build();
    }
}
