package com.project.personalfinancemanager.repository;

import com.project.personalfinancemanager.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity,Long> {
    Optional<UserProfileEntity> findByEmail(String email);

    Optional<UserProfileEntity> findByActivationToken(String activationToken);
}
