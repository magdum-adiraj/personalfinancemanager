package com.project.personalfinancemanager.controller;

import com.project.personalfinancemanager.dto.AuthDTO;
import com.project.personalfinancemanager.dto.UserProfileDTO;
import com.project.personalfinancemanager.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/register")
    public ResponseEntity<UserProfileDTO> registerUser(@RequestBody UserProfileDTO userProfileDTO){
        UserProfileDTO userProfile = userProfileService.registerProfile(userProfileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        if(userProfileService.activateProfile(token)){
            return ResponseEntity.ok("Profile activated successfully");
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO){
        try{
            if(!userProfileService.isProfileActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Account is inactive. Please activate your account first"));
            }
            return ResponseEntity.ok(userProfileService.authenticateAndGenerateToken(authDTO));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));
        }
    }
}
