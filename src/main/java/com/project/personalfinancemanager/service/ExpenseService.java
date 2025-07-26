package com.project.personalfinancemanager.service;

import com.project.personalfinancemanager.dto.ExpenseDTO;
import com.project.personalfinancemanager.entity.CategoryEntity;
import com.project.personalfinancemanager.entity.ExpenseEntity;
import com.project.personalfinancemanager.entity.UserProfileEntity;
import com.project.personalfinancemanager.repository.CategoryRepository;
import com.project.personalfinancemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO expense){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        CategoryEntity category = categoryRepository.findById(expense.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEntity(expense,profile,category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteExpense(Long expenseId){
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(()->new RuntimeException("Expense not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(entity);
    }

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(UserProfileEntity profile){
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalExpenseForCurrentUser(UserProfileEntity profile) {
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    private ExpenseEntity toEntity(ExpenseDTO dto, UserProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity expense){
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "NA")
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
