package com.project.personalfinancemanager.service;

import com.project.personalfinancemanager.dto.IncomeDTO;
import com.project.personalfinancemanager.entity.CategoryEntity;
import com.project.personalfinancemanager.entity.IncomeEntity;
import com.project.personalfinancemanager.entity.UserProfileEntity;
import com.project.personalfinancemanager.repository.CategoryRepository;
import com.project.personalfinancemanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final UserProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO income){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        CategoryEntity category = categoryRepository.findById(income.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found"));
        IncomeEntity newIncome = toEntity(income,profile,category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(()->new RuntimeException("Income not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(entity);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser(UserProfileEntity profile){
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomeForCurrentUser(UserProfileEntity profile) {
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate,String keyword, Sort sort){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return incomes.stream().map(this::toDTO).toList();
    }

    private IncomeEntity toEntity(IncomeDTO dto, UserProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity income){
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : "NA")
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}
