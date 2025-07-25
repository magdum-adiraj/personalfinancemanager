package com.project.personalfinancemanager.service;

import com.project.personalfinancemanager.dto.CategoryDTO;
import com.project.personalfinancemanager.entity.CategoryEntity;
import com.project.personalfinancemanager.entity.UserProfileEntity;
import com.project.personalfinancemanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserProfileService profileService;
    private final CategoryRepository categoryRepository;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Category with this name already exists");
        }
         CategoryEntity newCategory = toEntity(categoryDTO,profile);
         newCategory = categoryRepository.save(newCategory);
         return toDTO(newCategory);
    }

    public List<CategoryDTO> getCategoriesForCurrentUser(){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileId(profile.getId());
        return categoryEntities.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categoryEntities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(()->new RuntimeException("Category not found or not accessible"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory.setType(dto.getType());
        existingCategory=categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }

    private CategoryEntity toEntity(CategoryDTO categoryDTO, UserProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity entity){
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId():null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();
    }
}
