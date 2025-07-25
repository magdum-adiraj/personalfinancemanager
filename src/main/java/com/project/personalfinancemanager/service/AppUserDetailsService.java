package com.project.personalfinancemanager.service;

import com.project.personalfinancemanager.entity.UserProfileEntity;
import com.project.personalfinancemanager.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserProfileEntity userProfile = profileRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Profile Not Found with email: "+email));
        return User.builder()
                .username(userProfile.getEmail())
                .password(userProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
