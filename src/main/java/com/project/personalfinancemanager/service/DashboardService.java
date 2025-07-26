package com.project.personalfinancemanager.service;

import com.project.personalfinancemanager.dto.ExpenseDTO;
import com.project.personalfinancemanager.dto.IncomeDTO;
import com.project.personalfinancemanager.dto.RecentTransactionDTO;
import com.project.personalfinancemanager.entity.UserProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;


@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final UserProfileService profileService;

    public Map<String,Object> getDashboardData() {
        UserProfileEntity profile = profileService.getCurrentUserProfile();
        Map<String,Object> response = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser(profile);
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser(profile);
        BigDecimal totalIncome = incomeService.getTotalIncomeForCurrentUser(profile);
        BigDecimal totalExpense = expenseService.getTotalExpenseForCurrentUser(profile);
        List<RecentTransactionDTO> recentTransactionDTOS = concat(latestIncomes.stream().map(income ->
                        RecentTransactionDTO.builder()
                                .id(income.getId())
                                .profileId(profile.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("income")
                                .build()),
                latestExpenses.stream().map(expense ->
                        RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("expense")
                                .build()))
                .sorted((a,b)->{
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp==0 && a.getCreatedAt()!=null && b.getCreatedAt()!=null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).toList();

        response.put("totalBalance",totalIncome.subtract(totalExpense));
        response.put("totalIncome",totalIncome);
        response.put("totalExpense",totalExpense);
        response.put("recentIncomes",latestIncomes);
        response.put("recentExpenses",latestExpenses);
        response.put("recentTransactions",recentTransactionDTOS);

        return response;
    }
}
