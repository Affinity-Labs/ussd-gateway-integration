package com.affinitylabs.models.middleware.accounts;

public record LoanAccounts(    String id,
                               String loanAccountNumber,
                               String mambuAccountKey,
                               String loanName,
                               String mambuClientKey,
                               String assignedBranchKey,
                               String assignedUserKey,
                               String accountState,
                               String creationDate,
                               String approvedDate,
                               String mambuProductKey,
                               double loanAmount,
                               String loanType) {
}
