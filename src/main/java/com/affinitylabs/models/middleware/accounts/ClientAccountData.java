package com.affinitylabs.models.middleware.accounts;

import java.util.List;

public record ClientAccountData(List<CustomerAccounts> depositAccounts,
                                List<LoanAccounts> loanAccounts) {
}
