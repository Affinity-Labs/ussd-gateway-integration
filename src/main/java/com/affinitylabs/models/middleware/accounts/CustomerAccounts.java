package com.affinitylabs.models.middleware.accounts;

public record CustomerAccounts(String accountId, String accountNumber, String accountState, String availableBalance, String accountName, String accountType, String accountAlias, String accountSubType, String currency, String maturityDate, CustomerAssignedBranch assignedBranch, String rolloverOption, String totalBalance, String lastModified, String dateCreated, CustomerAccountInterestDetail interestDetail) {
}
