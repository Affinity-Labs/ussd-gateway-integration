package com.affinitylabs.models.middleware.transactions;

import java.util.List;

public record QueryServiceResponse(String accountName, List<QueryServiceOptions> options) {
}
