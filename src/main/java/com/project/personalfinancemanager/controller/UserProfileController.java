package com.project.personalfinancemanager.controller;

import com.project.personalfinancemanager.dto.UserProfileDTO;
import com.project.personalfinancemanager.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
