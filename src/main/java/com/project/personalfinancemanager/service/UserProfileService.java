package com.project.personalfinancemanager.service;

import com.project.personalfinancemanager.dto.AuthDTO;
import com.project.personalfinancemanager.dto.UserProfileDTO;
import com.project.personalfinancemanager.entity.UserProfileEntity;
import com.project.personalfinancemanager.repository.UserProfileRepository;
import com.project.personalfinancemanager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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

    public boolean isProfileActive(String email){
        return userProfileRepository.findByEmail(email)
                .map(UserProfileEntity::getIsActive)
                .orElse(false);
    }

    public UserProfileEntity getCurrentUserProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userProfileRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("Profile Not Found with email: "+authentication.getName()));
    }

    public UserProfileDTO getPublicUserProfile(String email){
        UserProfileEntity currentUser = null;
        if(email==null){
            currentUser=getCurrentUserProfile();
        }else{
            currentUser=userProfileRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("Profile Not Found with email: "+email));
        }
        return toDTO(currentUser);
    }

    public Map<String,Object> authenticateAndGenerateToken(AuthDTO authDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
            String token = JwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token",token,
                    "user",getPublicUserProfile(authDTO.getEmail())
            );
        } catch (Exception e){
            throw new RuntimeException("Invalid email or password");
        }
    }

    public UserProfileEntity toEntity(UserProfileDTO userProfileDTO){
        return UserProfileEntity.builder()
                .id(userProfileDTO.getId())
                .fullName(userProfileDTO.getFullName())
                .email(userProfileDTO.getEmail())
                .password(passwordEncoder.encode(userProfileDTO.getPassword()))
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
